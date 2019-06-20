package moze_intel.projecte;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.config.TomeEnabledCondition;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.customRecipes.PhilStoneSmeltingHelper;
import moze_intel.projecte.gameObjs.entity.EntityFireProjectile;
import moze_intel.projecte.gameObjs.entity.EntityHomingArrow;
import moze_intel.projecte.gameObjs.entity.EntityLavaProjectile;
import moze_intel.projecte.gameObjs.entity.EntityLensProjectile;
import moze_intel.projecte.gameObjs.entity.EntityMobRandomizer;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import moze_intel.projecte.gameObjs.entity.EntitySWRGProjectile;
import moze_intel.projecte.gameObjs.entity.EntityWaterProjectile;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.handlers.InternalAbilities;
import moze_intel.projecte.handlers.InternalTimers;
import moze_intel.projecte.impl.AlchBagImpl;
import moze_intel.projecte.impl.IMCHandler;
import moze_intel.projecte.impl.KnowledgeImpl;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.integration.curios.CuriosIntegration;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.ThreadCheckUUID;
import moze_intel.projecte.network.ThreadCheckUpdate;
import moze_intel.projecte.network.commands.*;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.rendering.ChestRenderer;
import moze_intel.projecte.rendering.CondenserMK2Renderer;
import moze_intel.projecte.rendering.CondenserRenderer;
import moze_intel.projecte.rendering.LayerYue;
import moze_intel.projecte.rendering.NovaCataclysmRenderer;
import moze_intel.projecte.rendering.NovaCatalystRenderer;
import moze_intel.projecte.rendering.PedestalRenderer;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.DummyIStorage;
import moze_intel.projecte.utils.GuiHandler;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

	public static final List<String> uuids = new ArrayList<>();

	public static void debugLog(String msg, Object... args)
	{
		if (DEV_ENVIRONMENT || ProjectEConfig.misc.debugLogging.get())
		{
			LOGGER.info(msg, args);
		} else
		{
			LOGGER.debug(msg, args);
		}
	}

	public PECore()
	{
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::clientSetup);
			FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::loadComplete);
			MinecraftForge.EVENT_BUS.addListener(ClientHandler::registerRenders);
		});

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcQueue);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcHandle);
		MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverQuit);
	}

	static class ClientHandler
	{
		static void clientSetup(FMLClientSetupEvent evt)
		{
			DeferredWorkQueue.runLater(() -> {
				ClientKeyHelper.registerKeyBindings();
			});
		}

		static void loadComplete(FMLLoadCompleteEvent evt)
		{
			// ClientSetup is too early to do this
			DeferredWorkQueue.runLater(() -> {
				Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
				PlayerRenderer render = skinMap.get("default");
				render.addLayer(new LayerYue(render));
				render = skinMap.get("slim");
				render.addLayer(new LayerYue(render));
			});
		}

		static void registerRenders(ModelRegistryEvent evt)
		{
			// Tile Entity
			ClientRegistry.bindTileEntitySpecialRenderer(AlchChestTile.class, new ChestRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(CondenserTile.class, new CondenserRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(CondenserMK2Tile.class, new CondenserMK2Renderer());
			ClientRegistry.bindTileEntitySpecialRenderer(DMPedestalTile.class, new PedestalRenderer());

			//Entities
			RenderingRegistry.registerEntityRenderingHandler(EntityWaterProjectile.class, createRenderFactoryForSnowball());
			RenderingRegistry.registerEntityRenderingHandler(EntityLavaProjectile.class, createRenderFactoryForSnowball());
			RenderingRegistry.registerEntityRenderingHandler(EntityMobRandomizer.class, createRenderFactoryForSnowball());
			RenderingRegistry.registerEntityRenderingHandler(EntityLensProjectile.class, createRenderFactoryForSnowball());
			RenderingRegistry.registerEntityRenderingHandler(EntityFireProjectile.class, createRenderFactoryForSnowball());
			RenderingRegistry.registerEntityRenderingHandler(EntitySWRGProjectile.class, createRenderFactoryForSnowball());
			RenderingRegistry.registerEntityRenderingHandler(EntityNovaCatalystPrimed.class, NovaCatalystRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityNovaCataclysmPrimed.class, NovaCataclysmRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityHomingArrow.class, TippedArrowRenderer::new);
		}

		private static <T extends Entity & IRendersAsItem> IRenderFactory<T> createRenderFactoryForSnowball()
		{
			return manager -> new SpriteRenderer<T>(manager, Minecraft.getInstance().getItemRenderer());
		}
	}

	private void commonSetup(FMLCommonSetupEvent event)
	{
		DEV_ENVIRONMENT = true; // TODO 1.13 ((Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));

		CONFIG_DIR = new File(new File("config"), MODNAME);

		if (!CONFIG_DIR.exists())
		{
			CONFIG_DIR.mkdirs();
		}

		ProjectEConfig.load();

		AlchBagImpl.init();
		KnowledgeImpl.init();
		CapabilityManager.INSTANCE.register(InternalTimers.class, new DummyIStorage<>(), InternalTimers::new);
		CapabilityManager.INSTANCE.register(InternalAbilities.class, new DummyIStorage<>(), () -> new InternalAbilities(null));

		if (ModList.get().isLoaded("curios"))
		{
			FMLJavaModLoadingContext.get().getModEventBus().register(CuriosIntegration.class);
			MinecraftForge.EVENT_BUS.register(CuriosIntegration.class);
		}

		new ThreadCheckUpdate().start();

		DeferredWorkQueue.runLater(() -> {
			PacketHandler.register(); // NetworkRegistry.createInstance

			// internals unsafe
			CraftingHelper.register(new ResourceLocation(PECore.MODID, "tome_enabled"), new TomeEnabledCondition());
		});
	}
	
	private void imcQueue(InterModEnqueueEvent event)
	{
		WorldTransmutations.init();
	}

	private void imcHandle(InterModProcessEvent event)
	{
		IMCHandler.handleMessages();
	}

	private void serverAboutToStart(FMLServerAboutToStartEvent event)
	{
		// I'd love for these to be parallel, but they have to run serially, and after vanilla's because
		// they look at vanilla's recipes
		event.getServer().getResourceManager().addReloadListener(new PhilStoneSmeltingHelper());
		event.getServer().getResourceManager().addReloadListener((IResourceManagerReloadListener) resourceManager -> {
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
	}

	private void serverQuit(FMLServerStoppedEvent event)
	{
		TransmutationOffline.cleanAll();
		Transmutation.clearCache();
		EMCMapper.clearMaps();
	}
}
