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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
	public static final String VERSION = "@VERSION@";
	public static final GameProfile FAKEPLAYER_GAMEPROFILE = new GameProfile(UUID.fromString("590e39c7-9fb6-471b-a4c2-c0e539b2423d"), "[" + MODNAME + "]");
	public static File CONFIG_DIR;
	public static File PREGENERATED_EMC_FILE;
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
		// ObjHandler in ctor since registry events fire before preinit
		MinecraftForge.EVENT_BUS.register(ObjHandler.class);

		FMLModLoadingContext.get().getModEventBus().addListener(this::preInit);
		FMLModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLModLoadingContext.get().getModEventBus().addListener(this::postInit);
		FMLModLoadingContext.get().getModEventBus().addListener(this::serverStarting);
		FMLModLoadingContext.get().getModEventBus().addListener(this::serverStopping);
		FMLModLoadingContext.get().getModEventBus().addListener(this::serverQuit);
	}

	private void preInit(FMLPreInitializationEvent event)
	{
		proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
		DEV_ENVIRONMENT = false; // TODO 1.13 ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));

		MinecraftForge.EVENT_BUS.register(PlayerEvents.class);
		MinecraftForge.EVENT_BUS.register(TickEvents.class);
		MinecraftForge.EVENT_BUS.register(SoundHandler.class);
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			MinecraftForge.EVENT_BUS.register(KeyPressEvent.class);
			MinecraftForge.EVENT_BUS.register(PlayerRender.class);
			MinecraftForge.EVENT_BUS.register(ToolTipEvent.class);
			MinecraftForge.EVENT_BUS.register(TransmutationRenderingEvent.class);
			MinecraftForge.EVENT_BUS.register(ClientProxy.class);
		});

		// todo 1.13 remove
		ObjHandler.registerTileEntities(new RegistryEvent.Register<>(new ResourceLocation("tileentities"), ForgeRegistries.TILE_ENTITIES));

		AlchBagImpl.init();
		KnowledgeImpl.init();
		CapabilityManager.INSTANCE.register(InternalTimers.class, new DummyIStorage<>(), InternalTimers::new);
		CapabilityManager.INSTANCE.register(InternalAbilities.class, new DummyIStorage<>(), () -> new InternalAbilities(null));

		CONFIG_DIR = new File(new File("config"), MODNAME);

		if (!CONFIG_DIR.exists())
		{
			CONFIG_DIR.mkdirs();
		}

		PREGENERATED_EMC_FILE = new File(CONFIG_DIR, "pregenerated_emc.json");

		PacketHandler.register();
		
		// TODO 1.13 NetworkRegistry.INSTANCE.registerGuiHandler(PECore.instance, new GuiHandler());

		proxy.registerKeyBinds();
		proxy.registerRenderers();
	}
	
	private void init(FMLInitializationEvent event)
	{
		CraftingHelper.register(new ResourceLocation(PECore.MODID, "tome_enabled"), new TomeEnabledCondition());
		proxy.registerLayerRenderers();
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
				.then(ReloadEmcCMD.register())
				.then(RemoveEmcCMD.register())
				.then(ResetEmcCMD.register())
				.then(SetEmcCMD.register())
				.then(ShowBagCMD.register());

		event.getCommandDispatcher().register(root);

		if (!ThreadCheckUUID.hasRunServer())
		{
			new ThreadCheckUUID(true).start();
		}

		long start = System.currentTimeMillis();

		CustomEMCParser.init();

		LOGGER.info("Starting server-side EMC mapping.");

		EMCMapper.map();

		LOGGER.info("Registered " + EMCMapper.emc.size() + " EMC values. (took " + (System.currentTimeMillis() - start) + " ms)");
	}

	private void serverStopping (FMLServerStoppingEvent event)
	{
		TransmutationOffline.cleanAll();
	}
	
	private void serverQuit(FMLServerStoppedEvent event)
	{
		Transmutation.clearCache();
		EMCMapper.clearMaps();
	}

	private void handleImc()
	{
		InterModComms.getMessages(MODID).forEach(IMCHandler::handleIMC);
	}

	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(MODID))
		{
			// TODO 1.13 ConfigManager.sync(MODID, Config.Type.INSTANCE);
		}
	}

	public static void refreshJEI()
	{
		if (ModList.get().isLoaded("jei"))
		{
			// todo 1.13 PEJeiPlugin.refresh();
		}
	}
}
