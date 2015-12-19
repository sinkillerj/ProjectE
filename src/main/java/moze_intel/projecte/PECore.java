package moze_intel.projecte;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.config.NBTWhitelistParser;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.events.ConnectionHandler;
import moze_intel.projecte.events.PlayerEvents;
import moze_intel.projecte.events.TickEvents;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.handlers.TileEntityHandler;
import moze_intel.projecte.impl.IMCHandler;
import moze_intel.projecte.integration.Integration;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.ThreadCheckUUID;
import moze_intel.projecte.network.ThreadCheckUpdate;
import moze_intel.projecte.network.commands.ProjectECMD;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.playerData.TransmutationOffline;
import moze_intel.projecte.proxies.IProxy;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.GuiHandler;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Mod(modid = PECore.MODID, name = PECore.MODNAME, version = PECore.VERSION)
public class PECore
{
	public static final String MODID = "ProjectE";
	public static final String MODNAME = "ProjectE";
	public static final String VERSION = "@VERSION@";
	public static final GameProfile FAKEPLAYER_GAMEPROFILE = new GameProfile(UUID.fromString("590e39c7-9fb6-471b-a4c2-c0e539b2423d"), "[ProjectE]");
	public static File CONFIG_DIR;
	public static File PREGENERATED_EMC_FILE;

	@Instance(MODID)
	public static PECore instance;
	
	@SidedProxy(clientSide = "moze_intel.projecte.proxies.ClientProxy", serverSide = "moze_intel.projecte.proxies.ServerProxy")
	public static IProxy proxy;

	public static final List<String> uuids = Lists.newArrayList();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		CONFIG_DIR = new File(event.getModConfigurationDirectory(), "ProjectE");
		
		if (!CONFIG_DIR.exists())
		{
			CONFIG_DIR.mkdirs();
		}

		PREGENERATED_EMC_FILE = new File(CONFIG_DIR, "pregenerated_emc.json");
		ProjectEConfig.init(new File(CONFIG_DIR, "ProjectE.cfg"));

		CustomEMCParser.init();

		NBTWhitelistParser.init();

		PacketHandler.register();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(PECore.instance, new GuiHandler());

		PlayerEvents pe = new PlayerEvents();
		MinecraftForge.EVENT_BUS.register(pe);
		FMLCommonHandler.instance().bus().register(pe);

		FMLCommonHandler.instance().bus().register(new TickEvents());
		FMLCommonHandler.instance().bus().register(new ConnectionHandler());

		proxy.registerClientOnlyEvents();

		ObjHandler.register();
		ObjHandler.addRecipes();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		proxy.registerKeyBinds();
		proxy.registerRenderers();
		AchievementHandler.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		ObjHandler.registerPhiloStoneSmelting();
		NBTWhitelistParser.readUserData();
		proxy.initializeManual();
		
		Integration.init();
	}
	
	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new ProjectECMD());

		if (!ThreadCheckUpdate.hasRunServer())
		{
			new ThreadCheckUpdate(true).start();
		}

		if (!ThreadCheckUUID.hasRunServer())
		{
			new ThreadCheckUUID(true).start();
		}

		long start = System.currentTimeMillis();

		CustomEMCParser.readUserData();

		PELogger.logInfo("Starting server-side EMC mapping.");

		EMCMapper.map();

		PELogger.logInfo("Registered " + EMCMapper.emc.size() + " EMC values. (took " + (System.currentTimeMillis() - start) + " ms)");
		
		File dir = new File(event.getServer().getEntityWorld().getSaveHandler().getWorldDirectory(), "ProjectE");
		
		if (!dir.exists())
		{
			dir.mkdirs(); 
		}
	}

	@Mod.EventHandler
	public void serverStopping (FMLServerStoppingEvent event)
	{
		TransmutationOffline.cleanAll();
	}
	
	@Mod.EventHandler
	public void serverQuit(FMLServerStoppedEvent event)
	{
		TileEntityHandler.clearAll();
		PELogger.logDebug("Cleared tile entity maps.");

		Transmutation.clearCache();
		PELogger.logDebug("Cleared cached tome knowledge");

		PlayerChecks.clearLists();
		PELogger.logDebug("Cleared player check-lists: server stopping.");

		EMCMapper.clearMaps();
		PELogger.logInfo("Completed server-stop actions.");
	}

	@Mod.EventHandler
	public void onIMCMessage(FMLInterModComms.IMCEvent event)
	{
		for (FMLInterModComms.IMCMessage msg : event.getMessages())
		{
			IMCHandler.handleIMC(msg);
		}
	}

	@Mod.EventHandler
	public void remap(FMLMissingMappingsEvent event) {
		for (FMLMissingMappingsEvent.MissingMapping mapping : event.get())
		{
			try
			{
				String subName = mapping.name.split(":")[1];
				if (mapping.type == GameRegistry.Type.ITEM)
				{
					Item remappedItem = GameRegistry.findItem(PECore.MODID, "item.pe_" + subName.substring(5)); // strip "item." off of subName
					if (remappedItem != null)
					{
						// legacy remap (adding pe_ prefix)
						mapping.remap(remappedItem);
					}
					else
					{
						// Space strip remap - ItemBlocks
						String newSubName = Constants.SPACE_STRIP_NAME_MAP.get(subName);
						remappedItem = GameRegistry.findItem(PECore.MODID, newSubName);

						if (remappedItem != null)
						{
							mapping.remap(remappedItem);
							PELogger.logInfo(String.format("Remapped ProjectE ItemBlock from %s to %s", mapping.name, PECore.MODID + ":" + newSubName));
						}
						else
						{
							PELogger.logFatal("Failed to remap ProjectE ItemBlock: " + mapping.name);
						}
					}
				}
				if (mapping.type == GameRegistry.Type.BLOCK)
				{
					// Space strip remap - Blocks
					String newSubName = Constants.SPACE_STRIP_NAME_MAP.get(subName);
					Block remappedBlock = GameRegistry.findBlock(PECore.MODID, newSubName);

					if (remappedBlock != null)
					{
						mapping.remap(remappedBlock);
						PELogger.logInfo(String.format("Remapped ProjectE Block from %s to %s", mapping.name, PECore.MODID + ":" + newSubName));
					}
					else
					{
						PELogger.logFatal("Failed to remap PE Block: " + mapping.name);
					}
				}
			} catch (Throwable t)
			{
				// Should never happen
				throw Throwables.propagate(t);
			}
		}
	}
}
