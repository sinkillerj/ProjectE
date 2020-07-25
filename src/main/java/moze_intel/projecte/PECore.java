package moze_intel.projecte;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
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
import moze_intel.projecte.emc.mappers.recipe.CraftingMapper;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.customRecipes.PhilStoneSmeltingHelper;
import moze_intel.projecte.gameObjs.items.ItemPE;
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
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.ThreadCheckUUID;
import moze_intel.projecte.network.ThreadCheckUpdate;
import moze_intel.projecte.network.commands.ClearKnowledgeCMD;
import moze_intel.projecte.network.commands.RemoveEmcCMD;
import moze_intel.projecte.network.commands.ResetEmcCMD;
import moze_intel.projecte.network.commands.SetEmcCMD;
import moze_intel.projecte.network.commands.ShowBagCMD;
import moze_intel.projecte.network.commands.argument.ColorArgument;
import moze_intel.projecte.network.commands.argument.NSSItemArgument;
import moze_intel.projecte.network.commands.argument.UUIDArgument;
import moze_intel.projecte.rendering.ChestRenderer;
import moze_intel.projecte.rendering.LayerYue;
import moze_intel.projecte.rendering.NovaRenderer;
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
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
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
	public static boolean DEV_ENVIRONMENT;
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static final List<String> uuids = new ArrayList<>();

	public static ModContainer MOD_CONTAINER;

	public static void debugLog(String msg, Object... args) {
		if (DEV_ENVIRONMENT || ProjectEConfig.common.debugLogging.get()) {
			LOGGER.info(msg, args);
		} else {
			LOGGER.debug(msg, args);
		}
	}

	public PECore() {
		MOD_CONTAINER = ModLoadingContext.get().getActiveContainer();

		DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::clientSetup);
			FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::loadComplete);
			FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientHandler::onStitch);
		});

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcQueue);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcHandle);
		MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::addReloadListenersLowest);
		MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
		MinecraftForge.EVENT_BUS.addListener(this::serverQuit);

		//Register our config files
		ProjectEConfig.register();
	}

	static class ClientHandler {

		static void clientSetup(FMLClientSetupEvent evt) {
			DeferredWorkQueue.runLater(ClientKeyHelper::registerKeyBindings);

			//Tile Entity
			ClientRegistry.bindTileEntityRenderer((TileEntityType<AlchChestTile>) ObjHandler.ALCH_CHEST_TILE, dispatcher -> new ChestRenderer(dispatcher, new ResourceLocation(PECore.MODID, "textures/blocks/alchemy_chest.png"), block -> block == ObjHandler.alchChest));
			ClientRegistry.bindTileEntityRenderer((TileEntityType<CondenserTile>) ObjHandler.CONDENSER_TILE, dispatcher -> new ChestRenderer(dispatcher, new ResourceLocation(PECore.MODID, "textures/blocks/condenser.png"), block -> block == ObjHandler.condenser));
			ClientRegistry.bindTileEntityRenderer((TileEntityType<CondenserMK2Tile>) ObjHandler.CONDENSER_MK2_TILE, dispatcher -> new ChestRenderer(dispatcher, new ResourceLocation(PECore.MODID, "textures/blocks/condenser_mk2.png"), block -> block == ObjHandler.condenserMk2));
			ClientRegistry.bindTileEntityRenderer((TileEntityType<DMPedestalTile>) ObjHandler.DM_PEDESTAL_TILE, PedestalRenderer::new);

			//Entities
			RenderingRegistry.registerEntityRenderingHandler(ObjHandler.WATER_PROJECTILE, WaterOrbRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ObjHandler.LAVA_PROJECTILE, LavaOrbRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ObjHandler.MOB_RANDOMIZER, RandomizerRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ObjHandler.LENS_PROJECTILE, ExplosiveLensRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ObjHandler.FIRE_PROJECTILE, FireballRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ObjHandler.SWRG_PROJECTILE, LightningRenderer::new);
			RenderingRegistry.registerEntityRenderingHandler(ObjHandler.NOVA_CATALYST_PRIMED, manager -> new NovaRenderer<>(manager, ObjHandler.novaCatalyst::getDefaultState));
			RenderingRegistry.registerEntityRenderingHandler(ObjHandler.NOVA_CATACLYSM_PRIMED, manager -> new NovaRenderer<>(manager, ObjHandler.novaCataclysm::getDefaultState));
			RenderingRegistry.registerEntityRenderingHandler(ObjHandler.HOMING_ARROW, TippedArrowRenderer::new);

			//Render layers
			RenderTypeLookup.setRenderLayer(ObjHandler.interdictionTorch, RenderType.getCutout());
			RenderTypeLookup.setRenderLayer(ObjHandler.interdictionTorchWall, RenderType.getCutout());

			//Property Overrides
			addPropertyOverrides(new ResourceLocation(PECore.MODID, "active"), ItemPE.ACTIVE_GETTER, ObjHandler.eternalDensity, ObjHandler.voidRing,
					ObjHandler.arcana, ObjHandler.angelSmite, ObjHandler.blackHole, ObjHandler.bodyStone, ObjHandler.harvestGod, ObjHandler.ignition,
					ObjHandler.lifeStone, ObjHandler.mindStone, ObjHandler.soulStone, ObjHandler.timeWatch, ObjHandler.zero);
			addPropertyOverrides(new ResourceLocation(PECore.MODID, "mode"), ItemPE.MODE_GETTER, ObjHandler.arcana, ObjHandler.swrg);
		}

		private static void addPropertyOverrides(ResourceLocation override, IItemPropertyGetter propertyGetter, Item... items) {
			for (Item item : items) {
				ItemModelsProperties.func_239418_a_(item, override, propertyGetter);
			}
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

		static void onStitch(TextureStitchEvent.Pre evt) {
			if (evt.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
				//If curios is loaded add the klein star slot icon the the block map as curios no longer does it automatically
				if (ModList.get().isLoaded(IntegrationHelper.CURIO_MODID)) {
					evt.addSprite(IntegrationHelper.CURIOS_KLEIN_STAR);
				}
			}
		}
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		DEV_ENVIRONMENT = FMLLoader.getNameFunction("srg").isPresent();

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

		new ThreadCheckUpdate().start();

		EMCMappingHandler.loadMappers();
		CraftingMapper.loadMappers();
		NBTManager.loadProcessors();

		DeferredWorkQueue.runLater(() -> {
			PacketHandler.register();

			// internals unsafe
			CraftingHelper.register(TomeEnabledCondition.SERIALIZER);
			ArgumentTypes.register(MODID + ":uuid", UUIDArgument.class, new ArgumentSerializer<>(UUIDArgument::new));
			ArgumentTypes.register(MODID + ":color", ColorArgument.class, new ArgumentSerializer<>(ColorArgument::new));
			ArgumentTypes.register(MODID + ":nss", NSSItemArgument.class, new ArgumentSerializer<>(NSSItemArgument::new));
		});
	}

	private void imcQueue(InterModEnqueueEvent event) {
		EntityRandomizerHelper.init();
		WorldTransmutations.init();
		NSSSerializer.init();
		IntegrationHelper.sendIMCMessages(event);
	}

	private void imcHandle(InterModProcessEvent event) {
		IMCHandler.handleMessages();
	}

	private void addReloadListeners(AddReloadListenerEvent event) {
		//Register the philo stone smelting helper at the regular event timing
		event.addListener(new PhilStoneSmeltingHelper());
	}

	private void addReloadListenersLowest(AddReloadListenerEvent event) {
		//Note: We register our listener for this event on lowest priority so that if other mods register custom NSSTags
		// or other things that need to be sync'd/reloaded they have a chance to go before we do
		event.addListener(new EMCReloadListener());
	}

	private void registerCommands(RegisterCommandsEvent event) {
		LiteralArgumentBuilder<CommandSource> root = Commands.literal("projecte")
				.then(ClearKnowledgeCMD.register())
				.then(RemoveEmcCMD.register())
				.then(ResetEmcCMD.register())
				.then(SetEmcCMD.register())
				.then(ShowBagCMD.register());
		event.getDispatcher().register(root);
	}

	private void serverStarting(FMLServerStartingEvent event) {
		if (!ThreadCheckUUID.hasRunServer()) {
			new ThreadCheckUUID(true).start();
		}
	}

	private void serverQuit(FMLServerStoppedEvent event) {
		TransmutationOffline.cleanAll();
		EMCMappingHandler.clearEmcMap();
	}
}