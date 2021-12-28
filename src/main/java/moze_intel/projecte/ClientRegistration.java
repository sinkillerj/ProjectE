package moze_intel.projecte;

import mezz.jei.api.runtime.IRecipesGui;
import moze_intel.projecte.events.TransmutationRenderingEvent;
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
import moze_intel.projecte.gameObjs.gui.PEContainerScreen;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
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
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

	public static final ResourceLocation ACTIVE_OVERRIDE = PECore.rl("active");
	public static final ResourceLocation MODE_OVERRIDE = PECore.rl("mode");

	@SubscribeEvent
	public static void registerContainerTypes(RegistryEvent.Register<MenuType<?>> event) {
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
		if (ModList.get().isLoaded("jei")) {
			//Note: This listener is only registered if JEI is loaded
			MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, (ScreenOpenEvent event) -> {
				if (Minecraft.getInstance().screen instanceof PEContainerScreen screen) {
					//If JEI is loaded and our current screen is a mekanism gui,
					// check if the new screen is a JEI recipe screen
					if (event.getScreen() instanceof IRecipesGui) {
						//If it is mark on our current screen that we are switching to JEI
						screen.switchingToJEI = true;
					}
				}
			});
		}
		OverlayRegistry.registerOverlayAbove(ForgeIngameGui.CROSSHAIR_ELEMENT, "PETransmutationResult", new TransmutationRenderingEvent());

		//Render layers
		ItemBlockRenderTypes.setRenderLayer(PEBlocks.INTERDICTION_TORCH.getBlock(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(PEBlocks.INTERDICTION_TORCH.getWallBlock(), RenderType.cutout());

		evt.enqueueWork(() -> {
			ClientKeyHelper.registerKeyBindings();
			//Property Overrides
			addPropertyOverrides(ACTIVE_OVERRIDE, (stack, world, entity, seed) -> ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE) ? 1F : 0F,
					PEItems.GEM_OF_ETERNAL_DENSITY, PEItems.VOID_RING, PEItems.ARCANA_RING, PEItems.ARCHANGEL_SMITE, PEItems.BLACK_HOLE_BAND, PEItems.BODY_STONE,
					PEItems.HARVEST_GODDESS_BAND, PEItems.IGNITION_RING, PEItems.LIFE_STONE, PEItems.MIND_STONE, PEItems.SOUL_STONE, PEItems.WATCH_OF_FLOWING_TIME,
					PEItems.ZERO_RING);
			addPropertyOverrides(MODE_OVERRIDE, (stack, world, entity, seed) -> stack.hasTag() ? stack.getOrCreateTag().getInt(Constants.NBT_KEY_MODE) : 0F,
					PEItems.ARCANA_RING, PEItems.SWIFTWOLF_RENDING_GALE);
		});
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		//Block Entity
		event.registerBlockEntityRenderer(PEBlockEntityTypes.ALCHEMICAL_CHEST.get(), context -> new ChestRenderer(context, PECore.rl("textures/block/alchemical_chest.png"), () -> PEBlocks.ALCHEMICAL_CHEST));
		event.registerBlockEntityRenderer(PEBlockEntityTypes.CONDENSER.get(), context -> new ChestRenderer(context, PECore.rl("textures/block/condenser_mk1.png"), () -> PEBlocks.CONDENSER));
		event.registerBlockEntityRenderer(PEBlockEntityTypes.CONDENSER_MK2.get(), context -> new ChestRenderer(context, PECore.rl("textures/block/condenser_mk2.png"), () -> PEBlocks.CONDENSER_MK2));
		event.registerBlockEntityRenderer(PEBlockEntityTypes.DARK_MATTER_PEDESTAL.get(), PedestalRenderer::new);

		//Entities
		event.registerEntityRenderer(PEEntityTypes.WATER_PROJECTILE.get(), WaterOrbRenderer::new);
		event.registerEntityRenderer(PEEntityTypes.LAVA_PROJECTILE.get(), LavaOrbRenderer::new);
		event.registerEntityRenderer(PEEntityTypes.MOB_RANDOMIZER.get(), RandomizerRenderer::new);
		event.registerEntityRenderer(PEEntityTypes.LENS_PROJECTILE.get(), ExplosiveLensRenderer::new);
		event.registerEntityRenderer(PEEntityTypes.FIRE_PROJECTILE.get(), FireballRenderer::new);
		event.registerEntityRenderer(PEEntityTypes.SWRG_PROJECTILE.get(), LightningRenderer::new);
		event.registerEntityRenderer(PEEntityTypes.NOVA_CATALYST_PRIMED.get(), context -> new NovaRenderer<>(context, PEBlocks.NOVA_CATALYST.getBlock()::defaultBlockState));
		event.registerEntityRenderer(PEEntityTypes.NOVA_CATACLYSM_PRIMED.get(), context -> new NovaRenderer<>(context, PEBlocks.NOVA_CATACLYSM.getBlock()::defaultBlockState));
		event.registerEntityRenderer(PEEntityTypes.HOMING_ARROW.get(), TippableArrowRenderer::new);
	}

	@SubscribeEvent
	public static void addLayers(EntityRenderersEvent.AddLayers event) {
		for (String skinName : event.getSkins()) {
			PlayerRenderer skin = event.getSkin(skinName);
			if (skin != null) {
				skin.addLayer(new LayerYue(skin));
			}
		}
	}

	@SubscribeEvent
	public static void onStitch(TextureStitchEvent.Pre evt) {
		if (evt.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
			//If curios is loaded add the klein star slot icon the the block map as curios no longer does it automatically
			if (ModList.get().isLoaded(IntegrationHelper.CURIO_MODID)) {
				evt.addSprite(IntegrationHelper.CURIOS_KLEIN_STAR);
			}
		}
	}

	private static void addPropertyOverrides(ResourceLocation override, ItemPropertyFunction propertyGetter, ItemLike... itemProviders) {
		for (ItemLike itemProvider : itemProviders) {
			ItemProperties.register(itemProvider.asItem(), override, propertyGetter);
		}
	}

	private static <C extends AbstractContainerMenu, U extends Screen & MenuAccess<C>> void registerScreen(ContainerTypeRegistryObject<C> type, ScreenConstructor<C, U> factory) {
		MenuScreens.register(type.get(), factory);
	}
}