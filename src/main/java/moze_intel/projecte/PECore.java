package moze_intel.projecte;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.config.TomeEnabledCondition;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.TimeWatch;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.impl.AlchBagImpl;
import moze_intel.projecte.impl.BlacklistProxyImpl;
import moze_intel.projecte.impl.EMCProxyImpl;
import moze_intel.projecte.impl.IMCHandler;
import moze_intel.projecte.impl.KnowledgeImpl;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.impl.TransmutationProxyImpl;
import moze_intel.projecte.integration.Integration;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.ThreadCheckUUID;
import moze_intel.projecte.network.commands.*;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.proxies.ClientProxy;
import moze_intel.projecte.proxies.IProxy;
import moze_intel.projecte.proxies.ServerProxy;
import moze_intel.projecte.utils.DummyIStorage;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Triple;
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

		FMLModLoadingContext.get().getModEventBus().addListener(this::preInit);
		FMLModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLModLoadingContext.get().getModEventBus().addListener(this::postInit);
		FMLModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverQuit);
	}

	private void preInit(FMLPreInitializationEvent event)
	{
		DEV_ENVIRONMENT = true; // TODO 1.13 ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));

		CONFIG_DIR = new File(new File("config"), MODNAME);

		if (!CONFIG_DIR.exists())
		{
			CONFIG_DIR.mkdirs();
		}

		ProjectEConfig.load();

		// TODO 1.13 NetworkRegistry.INSTANCE.registerGuiHandler(PECore.instance, new GuiHandler());

		DeferredWorkQueue.enqueueWork(() -> {
			// todo 1.13 remove
			ObjHandler.registerTileEntities(new RegistryEvent.Register<>(new ResourceLocation("tileentities"), ForgeRegistries.TILE_ENTITIES));
			ObjHandler.registerEntities(new RegistryEvent.Register<>(new ResourceLocation("entities"), ForgeRegistries.ENTITIES));

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
		WorldTransmutations.init();

		DeferredWorkQueue.enqueueWork(() -> {
			CraftingHelper.register(new ResourceLocation(PECore.MODID, "tome_enabled"), new TomeEnabledCondition());
			proxy.registerLayerRenderers();
			return null;
		});
	}

	private void postInit(FMLPostInitializationEvent event)
	{
		Integration.init();
		IMCHandler.handleMessages();
	}

	private void loadComplete(FMLLoadCompleteEvent event)
	{
		// Transfer all thread-safe staging data to their single-threaded home for the life of the game
		WorldHelper.setInterdictionBlacklist(BlacklistProxyImpl.instance.getInterdictionBlacklist());
		WorldHelper.setSwrgBlacklist(BlacklistProxyImpl.instance.getSwrgBlacklist());
		TimeWatch.setInternalBlacklist(BlacklistProxyImpl.instance.getTimeWatchBlacklist());
		WorldTransmutations.setWorldTransmutation(TransmutationProxyImpl.instance.getWorldTransmutations());
		for (Triple<String, Object, Long> t : EMCProxyImpl.instance.getCustomEmcStaging())
		{
			APICustomEMCMapper.instance.registerCustomEMC(t.getLeft(), t.getMiddle(), t.getRight());
		}
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

	public static void refreshJEI()
	{
		if (ModList.get().isLoaded("jei"))
		{
			// todo 1.13 PEJeiPlugin.refresh();
		}
	}
}
