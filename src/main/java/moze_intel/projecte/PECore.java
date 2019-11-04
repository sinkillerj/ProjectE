package moze_intel.projecte;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IAlchBagItem;
import moze_intel.projecte.api.capabilities.item.IAlchChestItem;
import moze_intel.projecte.api.capabilities.item.IExtraFunction;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.api.capabilities.item.IProjectileShooter;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.config.TomeEnabledCondition;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.EMCReloadListener;
import moze_intel.projecte.emc.json.NSSSerializer;
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
import moze_intel.projecte.impl.IMCHandler;
import moze_intel.projecte.impl.TransmutationOffline;
import moze_intel.projecte.impl.capability.AlchBagImpl;
import moze_intel.projecte.impl.capability.AlchBagItemDefaultImpl;
import moze_intel.projecte.impl.capability.AlchChestItemDefaultImpl;
import moze_intel.projecte.impl.capability.ChargeItemDefaultImpl;
import moze_intel.projecte.impl.capability.EmcHolderItemDefaultImpl;
import moze_intel.projecte.impl.capability.EmcStorageDefaultImpl;
import moze_intel.projecte.impl.capability.ExtraFunctionItemDefaultImpl;
import moze_intel.projecte.impl.capability.KnowledgeImpl;
import moze_intel.projecte.impl.capability.ModeChangerItemDefaultImpl;
import moze_intel.projecte.impl.capability.PedestalItemDefaultImpl;
import moze_intel.projecte.impl.capability.ProjectileShooterItemDefaultImpl;
import moze_intel.projecte.integration.curios.CuriosIntegration;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.ThreadCheckUUID;
import moze_intel.projecte.network.ThreadCheckUpdate;
import moze_intel.projecte.network.commands.ClearKnowledgeCMD;
import moze_intel.projecte.network.commands.RemoveEmcCMD;
import moze_intel.projecte.network.commands.ResetEmcCMD;
import moze_intel.projecte.network.commands.SetEmcCMD;
import moze_intel.projecte.network.commands.ShowBagCMD;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.rendering.ChestRenderer;
import moze_intel.projecte.rendering.CondenserMK2Renderer;
import moze_intel.projecte.rendering.CondenserRenderer;
import moze_intel.projecte.rendering.LayerYue;
import moze_intel.projecte.rendering.NovaCataclysmRenderer;
import moze_intel.projecte.rendering.NovaCatalystRenderer;
import moze_intel.projecte.rendering.PedestalRenderer;
import moze_intel.projecte.rendering.entity.ExplosiveLensRenderer;
import moze_intel.projecte.rendering.entity.FireballRenderer;
import moze_intel.projecte.rendering.entity.LavaOrbRenderer;
import moze_intel.projecte.rendering.entity.LightningRenderer;
import moze_intel.projecte.rendering.entity.RandomizerRenderer;
import moze_intel.projecte.rendering.entity.WaterOrbRenderer;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.DummyIStorage;
import moze_intel.projecte.utils.EntityRandomizerHelper;
import moze_intel.projecte.utils.IntegrationHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PECore.MODID)
@Mod.EventBusSubscriber(modid = PECore.MODID)
public class PECore {

	public static final String MODID = ProjectEAPI.PROJECTE_MODID;
	public static final String MODNAME = "ProjectE";
	public static final GameProfile FAKEPLAYER_GAMEPROFILE = new GameProfile(UUID.fromString("590e39c7-9fb6-471b-a4c2-c0e539b2423d"), "[" + MODNAME + "]");
	public static File CONFIG_DIR;
	public static boolean DEV_ENVIRONMENT;
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final List<String> uuids = new ArrayList<>();

	public static void debugLog(String msg, Object... args) {
		if (DEV_ENVIRONMENT || ProjectEConfig.misc.debugLogging.get()) {
			LOGGER.info(msg, args);
		} else {
			LOGGER.debug(msg, args);
		}
	}

	public PECore() {
		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::clientSetup);
			FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::loadComplete);
		});

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcQueue);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcHandle);
		MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::serverAboutToStartLowest);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverQuit);
	}

	static class ClientHandler {

		static void clientSetup(FMLClientSetupEvent evt) {
			DeferredWorkQueue.runLater(ClientKeyHelper::registerKeyBindings);

			//Tile Entity
			ClientRegistry.bindTileEntitySpecialRenderer(AlchChestTile.class, new ChestRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(CondenserTile.class, new CondenserRenderer());
			ClientRegistry.bindTileEntitySpecialRenderer(CondenserMK2Tile.class, new CondenserMK2Renderer());
			ClientRegistry.bindTileEntitySpecialRenderer(DMPedestalTile.class, new PedestalRenderer());

			//Entities
			RenderingRegistry.registerEntityRenderingHandler(EntityWaterProjectile.class, WaterOrbRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityLavaProjectile.class, LavaOrbRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityMobRandomizer.class, RandomizerRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityLensProjectile.class, ExplosiveLensRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityFireProjectile.class, FireballRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntitySWRGProjectile.class, LightningRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityNovaCatalystPrimed.class, NovaCatalystRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityNovaCataclysmPrimed.class, NovaCataclysmRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(EntityHomingArrow.class, TippedArrowRenderer::new);
		}

		static void loadComplete(FMLLoadCompleteEvent evt) {
			// ClientSetup is too early to do this
			DeferredWorkQueue.runLater(() -> {
				Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
				PlayerRenderer render = skinMap.get("default");
				render.addLayer(new LayerYue(render));
				render = skinMap.get("slim");
				render.addLayer(new LayerYue(render));
			});
		}
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		DEV_ENVIRONMENT = FMLLoader.getNameFunction("srg").isPresent();

		CONFIG_DIR = new File(new File("config"), MODNAME);

		if (!CONFIG_DIR.exists()) {
			CONFIG_DIR.mkdirs();
		}

		ProjectEConfig.load();

		AlchBagImpl.init();
		KnowledgeImpl.init();
		CapabilityManager.INSTANCE.register(InternalTimers.class, new DummyIStorage<>(), InternalTimers::new);
		CapabilityManager.INSTANCE.register(InternalAbilities.class, new DummyIStorage<>(), () -> new InternalAbilities(null));
		CapabilityManager.INSTANCE.register(IAlchBagItem.class, new DummyIStorage<>(), AlchBagItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IAlchChestItem.class, new DummyIStorage<>(), AlchChestItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IExtraFunction.class, new DummyIStorage<>(), ExtraFunctionItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IItemCharge.class, new DummyIStorage<>(), ChargeItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IItemEmcHolder.class, new DummyIStorage<>(), EmcHolderItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IModeChanger.class, new DummyIStorage<>(), ModeChangerItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IPedestalItem.class, new DummyIStorage<>(), PedestalItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IProjectileShooter.class, new DummyIStorage<>(), ProjectileShooterItemDefaultImpl::new);
		CapabilityManager.INSTANCE.register(IEmcStorage.class, new DummyIStorage<>(), EmcStorageDefaultImpl::new);

		if (ModList.get().isLoaded(IntegrationHelper.CURIO_MODID)) {
			FMLJavaModLoadingContext.get().getModEventBus().register(CuriosIntegration.class);
			MinecraftForge.EVENT_BUS.register(CuriosIntegration.class);
		}

		new ThreadCheckUpdate().start();

		DeferredWorkQueue.runLater(() -> {
			PacketHandler.register();

			// internals unsafe
			CraftingHelper.register(TomeEnabledCondition.SERIALIZER);
		});
	}

	private void imcQueue(InterModEnqueueEvent event) {
		EntityRandomizerHelper.init();
		WorldTransmutations.init();
		NSSSerializer.init();
	}

	private void imcHandle(InterModProcessEvent event) {
		IMCHandler.handleMessages();
	}

	private void serverAboutToStart(FMLServerAboutToStartEvent event) {
		//Register the philo stone smelting helper at the regular event timing
		event.getServer().getResourceManager().addReloadListener(new PhilStoneSmeltingHelper());
	}

	private void serverAboutToStartLowest(FMLServerAboutToStartEvent event) {
		//Note: We register our listener for this event on lowest priority so that if other mods register custom NSSTags
		// or other things that need to be sync'd/reloaded they have a chance to go before we do
		event.getServer().getResourceManager().addReloadListener(new EMCReloadListener());
	}

	private void serverStarting(FMLServerStartingEvent event) {
		LiteralArgumentBuilder<CommandSource> root = Commands.literal("projecte")
				.then(ClearKnowledgeCMD.register())
				.then(RemoveEmcCMD.register())
				.then(ResetEmcCMD.register())
				.then(SetEmcCMD.register())
				.then(ShowBagCMD.register());

		event.getCommandDispatcher().register(root);

		if (!ThreadCheckUUID.hasRunServer()) {
			new ThreadCheckUUID(true).start();
		}
	}

	private void serverQuit(FMLServerStoppedEvent event) {
		TransmutationOffline.cleanAll();
		Transmutation.clearCache();
		EMCMappingHandler.clearMaps();
	}
}