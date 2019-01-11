package moze_intel.projecte;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.config.NBTWhitelistParser;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.config.TomeEnabledCondition;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.events.*;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.impl.AlchBagImpl;
import moze_intel.projecte.impl.IMCHandler;
import moze_intel.projecte.impl.KnowledgeImpl;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.integration.Integration;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.ThreadCheckUUID;
import moze_intel.projecte.network.commands.*;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.proxies.ClientProxy;
import moze_intel.projecte.proxies.IProxy;
import moze_intel.projecte.proxies.ServerProxy;
import moze_intel.projecte.utils.DummyIStorage;
import moze_intel.projecte.utils.SoundHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod(PECore.MODID)
@Mod.EventBusSubscriber(modid = PECore.MODID)
public class PECore
{
	public static final String MODID = "projecte";
	public static final String MODNAME = "ProjectE";
	public static final GameProfile FAKEPLAYER_GAMEPROFILE = new GameProfile(UUID.fromString("590e39c7-9fb6-471b-a4c2-c0e539b2423d"), "[" + MODNAME + "]");
	public static File CONFIG_DIR;
	public static boolean DEV_ENVIRONMENT;
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static IProxy proxy;

	public static final List<String> uuids = new ArrayList<>();

	public static void debugLog(String msg, Object... args)
	{
		if (DEV_ENVIRONMENT || ProjectEConfig.misc.debugLogging)
		{
			LOGGER.info(msg, args);
		} else
		{
			LOGGER.debug(msg, args);
		}
	}

	public PECore()
	{
		proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

		// In ctor since registry events fire before preinit
		MinecraftForge.EVENT_BUS.register(ObjHandler.class);
		MinecraftForge.EVENT_BUS.register(SoundHandler.class);

		FMLModLoadingContext.get().getModEventBus().addListener(this::preInit);
		FMLModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLModLoadingContext.get().getModEventBus().addListener(this::postInit);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverQuit);
	}

	private void preInit(FMLPreInitializationEvent event)
	{
		DEV_ENVIRONMENT = true; // TODO 1.13 ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));

		// todo 1.13 remove
		ObjHandler.registerTileEntities(new RegistryEvent.Register<>(new ResourceLocation("tileentities"), ForgeRegistries.TILE_ENTITIES));

		CONFIG_DIR = new File(new File("config"), MODNAME);

		if (!CONFIG_DIR.exists())
		{
			CONFIG_DIR.mkdirs();
		}

		ProjectEConfig.load();

		// TODO 1.13 NetworkRegistry.INSTANCE.registerGuiHandler(PECore.instance, new GuiHandler());

		// Thread unsafe stuff in here
		DeferredWorkQueue.enqueueWork(() -> {
			// Caps internals unsafe
			AlchBagImpl.init();
			KnowledgeImpl.init();
			CapabilityManager.INSTANCE.register(InternalTimers.class, new DummyIStorage<>(), InternalTimers::new);
			CapabilityManager.INSTANCE.register(InternalAbilities.class, new DummyIStorage<>(), () -> new InternalAbilities(null));

			PacketHandler.register(); // NetworkRegistry.createInstance
			proxy.registerKeyBinds(); // vanilla keybind array unsafe
			return null;
		});
	}
	
	private void init(FMLInitializationEvent event)
	{
		DeferredWorkQueue.enqueueWork(() -> {
			CraftingHelper.register(new ResourceLocation(PECore.MODID, "tome_enabled"), new TomeEnabledCondition());
			proxy.registerLayerRenderers();
			return null;
		});
	}

	private void postInit(FMLPostInitializationEvent event)
	{
		NBTWhitelistParser.init();
		Integration.init();
		handleImc();
	}
	
	private void serverStarting(FMLServerStartingEvent event)
	{
		LiteralArgumentBuilder<CommandSource> root = Commands.literal("projecte")
				.then(ClearKnowledgeCMD.register())
				.then(RemoveEmcCMD.register())
				.then(ResetEmcCMD.register())
				.then(SetEmcCMD.register())
				.then(ShowBagCMD.register());

		event.getCommandDispatcher().register(root);

		if (!ThreadCheckUUID.hasRunServer())
		{
			new ThreadCheckUUID(true).start();
		}

		event.getServer().getResourceManager().addReloadListener(resourceManager -> {
			long start = System.currentTimeMillis();

			CustomEMCParser.init();

			try {
				EMCMapper.map(resourceManager);
				LOGGER.info("Registered " + EMCMapper.emc.size() + " EMC values. (took " + (System.currentTimeMillis() - start) + " ms)");
				PacketHandler.sendFragmentedEmcPacketToAll();
			} catch (Throwable t)
			{
				LOGGER.error("Error calculating EMC values", t);
			}
		});
	}

	private void serverQuit(FMLServerStoppedEvent event)
	{
		TransmutationOffline.cleanAll();
		Transmutation.clearCache();
		EMCMapper.clearMaps();
	}

	private void handleImc()
	{
		InterModComms.getMessages(MODID).forEach(IMCHandler::handleIMC);
	}

	public static void refreshJEI()
	{
		if (ModList.get().isLoaded("jei"))
		{
			// todo 1.13 PEJeiPlugin.refresh();
		}
	}
}
