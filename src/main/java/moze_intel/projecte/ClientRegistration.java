package moze_intel.projecte;

import java.util.Map;
import moze_intel.projecte.gameObjs.gui.AbstractCollectorScreen;
import moze_intel.projecte.gameObjs.gui.AbstractCondenserScreen;
import moze_intel.projecte.gameObjs.gui.AlchBagScreen;
import moze_intel.projecte.gameObjs.gui.AlchChestScreen;
import moze_intel.projecte.gameObjs.gui.GUIDMFurnace;
import moze_intel.projecte.gameObjs.gui.GUIEternalDensity;
import moze_intel.projecte.gameObjs.gui.GUIMercurialEye;
import moze_intel.projecte.gameObjs.gui.GUIRMFurnace;
import moze_intel.projecte.gameObjs.gui.GUIRelay.GUIRelayMK1;
import moze_intel.projecte.gameObjs.gui.GUIRelay.GUIRelayMK2;
import moze_intel.projecte.gameObjs.gui.GUIRelay.GUIRelayMK3;
import moze_intel.projecte.gameObjs.gui.GUITransmutation;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import moze_intel.projecte.integration.IntegrationHelper;
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
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.TippedArrowRenderer;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

	public static final ResourceLocation ACTIVE_OVERRIDE = PECore.rl("active");
	public static final ResourceLocation MODE_OVERRIDE = PECore.rl("mode");

	@SubscribeEvent
	public static void registerContainerTypes(RegistryEvent.Register<ContainerType<?>> event) {
		registerScreen(PEContainerTypes.RM_FURNACE_CONTAINER, GUIRMFurnace::new);
		registerScreen(PEContainerTypes.DM_FURNACE_CONTAINER, GUIDMFurnace::new);
		registerScreen(PEContainerTypes.CONDENSER_CONTAINER, AbstractCondenserScreen.MK1::new);
		registerScreen(PEContainerTypes.CONDENSER_MK2_CONTAINER, AbstractCondenserScreen.MK2::new);
		registerScreen(PEContainerTypes.ALCH_CHEST_CONTAINER, AlchChestScreen::new);
		registerScreen(PEContainerTypes.ALCH_BAG_CONTAINER, AlchBagScreen::new);
		registerScreen(PEContainerTypes.ETERNAL_DENSITY_CONTAINER, GUIEternalDensity::new);
		registerScreen(PEContainerTypes.TRANSMUTATION_CONTAINER, GUITransmutation::new);
		registerScreen(PEContainerTypes.RELAY_MK1_CONTAINER, GUIRelayMK1::new);
		registerScreen(PEContainerTypes.RELAY_MK2_CONTAINER, GUIRelayMK2::new);
		registerScreen(PEContainerTypes.RELAY_MK3_CONTAINER, GUIRelayMK3::new);
		registerScreen(PEContainerTypes.COLLECTOR_MK1_CONTAINER, AbstractCollectorScreen.MK1::new);
		registerScreen(PEContainerTypes.COLLECTOR_MK2_CONTAINER, AbstractCollectorScreen.MK2::new);
		registerScreen(PEContainerTypes.COLLECTOR_MK3_CONTAINER, AbstractCollectorScreen.MK3::new);
		registerScreen(PEContainerTypes.MERCURIAL_EYE_CONTAINER, GUIMercurialEye::new);
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent evt) {
		//Tile Entity
		ClientRegistry.bindTileEntityRenderer(PETileEntityTypes.ALCHEMICAL_CHEST.get(), dispatcher -> new ChestRenderer(dispatcher, PECore.rl("textures/block/alchemical_chest.png"), block -> block == PEBlocks.ALCHEMICAL_CHEST.getBlock()));
		ClientRegistry.bindTileEntityRenderer(PETileEntityTypes.CONDENSER.get(), dispatcher -> new ChestRenderer(dispatcher, PECore.rl("textures/block/condenser_mk1.png"), block -> block == PEBlocks.CONDENSER.getBlock()));
		ClientRegistry.bindTileEntityRenderer(PETileEntityTypes.CONDENSER_MK2.get(), dispatcher -> new ChestRenderer(dispatcher, PECore.rl("textures/block/condenser_mk2.png"), block -> block == PEBlocks.CONDENSER_MK2.getBlock()));
		ClientRegistry.bindTileEntityRenderer(PETileEntityTypes.DARK_MATTER_PEDESTAL.get(), PedestalRenderer::new);

		//Entities
		RenderingRegistry.registerEntityRenderingHandler(PEEntityTypes.WATER_PROJECTILE.get(), WaterOrbRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(PEEntityTypes.LAVA_PROJECTILE.get(), LavaOrbRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(PEEntityTypes.MOB_RANDOMIZER.get(), RandomizerRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(PEEntityTypes.LENS_PROJECTILE.get(), ExplosiveLensRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(PEEntityTypes.FIRE_PROJECTILE.get(), FireballRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(PEEntityTypes.SWRG_PROJECTILE.get(), LightningRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(PEEntityTypes.NOVA_CATALYST_PRIMED.get(), manager -> new NovaRenderer<>(manager, PEBlocks.NOVA_CATALYST.getBlock()::defaultBlockState));
		RenderingRegistry.registerEntityRenderingHandler(PEEntityTypes.NOVA_CATACLYSM_PRIMED.get(), manager -> new NovaRenderer<>(manager, PEBlocks.NOVA_CATACLYSM.getBlock()::defaultBlockState));
		RenderingRegistry.registerEntityRenderingHandler(PEEntityTypes.HOMING_ARROW.get(), TippedArrowRenderer::new);

		//Render layers
		RenderTypeLookup.setRenderLayer(PEBlocks.INTERDICTION_TORCH.getBlock(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(PEBlocks.INTERDICTION_TORCH.getWallBlock(), RenderType.cutout());

		evt.enqueueWork(() -> {
			ClientKeyHelper.registerKeyBindings();
			//Property Overrides
			addPropertyOverrides(ACTIVE_OVERRIDE, (stack, world, entity) -> ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE) ? 1F : 0F,
					PEItems.GEM_OF_ETERNAL_DENSITY, PEItems.VOID_RING, PEItems.ARCANA_RING, PEItems.ARCHANGEL_SMITE, PEItems.BLACK_HOLE_BAND, PEItems.BODY_STONE,
					PEItems.HARVEST_GODDESS_BAND, PEItems.IGNITION_RING, PEItems.LIFE_STONE, PEItems.MIND_STONE, PEItems.SOUL_STONE, PEItems.WATCH_OF_FLOWING_TIME,
					PEItems.ZERO_RING);
			addPropertyOverrides(MODE_OVERRIDE, (stack, world, entity) -> stack.hasTag() ? stack.getOrCreateTag().getInt(Constants.NBT_KEY_MODE) : 0F,
					PEItems.ARCANA_RING, PEItems.SWIFTWOLF_RENDING_GALE);
		});
	}

	@SubscribeEvent
	public static void loadComplete(FMLLoadCompleteEvent evt) {
		// ClientSetup is too early to do this
		evt.enqueueWork(() -> {
			Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
			PlayerRenderer render = skinMap.get("default");
			render.addLayer(new LayerYue(render));
			render = skinMap.get("slim");
			render.addLayer(new LayerYue(render));
		});
	}

	@SubscribeEvent
	public static void onStitch(TextureStitchEvent.Pre evt) {
		if (evt.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
			//If curios is loaded add the klein star slot icon the the block map as curios no longer does it automatically
			if (ModList.get().isLoaded(IntegrationHelper.CURIO_MODID)) {
				evt.addSprite(IntegrationHelper.CURIOS_KLEIN_STAR);
			}
		}
	}

	private static void addPropertyOverrides(ResourceLocation override, IItemPropertyGetter propertyGetter, IItemProvider... itemProviders) {
		for (IItemProvider itemProvider : itemProviders) {
			ItemModelsProperties.register(itemProvider.asItem(), override, propertyGetter);
		}
	}

	private static <C extends Container, U extends Screen & IHasContainer<C>> void registerScreen(ContainerTypeRegistryObject<C> type, IScreenFactory<C, U> factory) {
		ScreenManager.register(type.get(), factory);
	}
}