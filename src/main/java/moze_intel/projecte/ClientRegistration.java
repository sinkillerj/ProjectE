package moze_intel.projecte;

import mezz.jei.api.runtime.IRecipesGui;
import moze_intel.projecte.gameObjs.container.DMFurnaceContainer;
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
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.rendering.ChestRenderer;
import moze_intel.projecte.rendering.EntitySpriteRenderer;
import moze_intel.projecte.rendering.LayerYue;
import moze_intel.projecte.rendering.NovaRenderer;
import moze_intel.projecte.rendering.PedestalRenderer;
import moze_intel.projecte.rendering.TransmutationRenderingOverlay;
import moze_intel.projecte.utils.ClientKeyHelper;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiOverlaysEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.overlay.VanillaGuiOverlay;
import net.neoforged.neoforge.common.NeoForge;

@Mod.EventBusSubscriber(modid = PECore.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientRegistration {

	public static final ResourceLocation ACTIVE_OVERRIDE = PECore.rl("active");
	public static final ResourceLocation MODE_OVERRIDE = PECore.rl("mode");

	@SubscribeEvent
	public static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(PEContainerTypes.RM_FURNACE_CONTAINER.get(), GUIRMFurnace::new);
		//noinspection RedundantTypeArguments (necessary for it to actually compile)
		event.<DMFurnaceContainer, GUIDMFurnace<DMFurnaceContainer>>register(PEContainerTypes.DM_FURNACE_CONTAINER.get(), GUIDMFurnace::new);
		event.register(PEContainerTypes.CONDENSER_CONTAINER.get(), AbstractCondenserScreen.MK1::new);
		event.register(PEContainerTypes.CONDENSER_MK2_CONTAINER.get(), AbstractCondenserScreen.MK2::new);
		event.register(PEContainerTypes.ALCH_CHEST_CONTAINER.get(), AlchChestScreen::new);
		event.register(PEContainerTypes.ALCH_BAG_CONTAINER.get(), AlchBagScreen::new);
		event.register(PEContainerTypes.ETERNAL_DENSITY_CONTAINER.get(), GUIEternalDensity::new);
		event.register(PEContainerTypes.TRANSMUTATION_CONTAINER.get(), GUITransmutation::new);
		event.register(PEContainerTypes.RELAY_MK1_CONTAINER.get(), GUIRelayMK1::new);
		event.register(PEContainerTypes.RELAY_MK2_CONTAINER.get(), GUIRelayMK2::new);
		event.register(PEContainerTypes.RELAY_MK3_CONTAINER.get(), GUIRelayMK3::new);
		event.register(PEContainerTypes.COLLECTOR_MK1_CONTAINER.get(), AbstractCollectorScreen.MK1::new);
		event.register(PEContainerTypes.COLLECTOR_MK2_CONTAINER.get(), AbstractCollectorScreen.MK2::new);
		event.register(PEContainerTypes.COLLECTOR_MK3_CONTAINER.get(), AbstractCollectorScreen.MK3::new);
		event.register(PEContainerTypes.MERCURIAL_EYE_CONTAINER.get(), GUIMercurialEye::new);
	}

	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent evt) {
		if (ModList.get().isLoaded("jei")) {
			//Note: This listener is only registered if JEI is loaded
			NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, (ScreenEvent.Opening event) -> {
				if (event.getCurrentScreen() instanceof PEContainerScreen<?> screen) {
					//If JEI is loaded and our current screen is a mekanism gui,
					// check if the new screen is a JEI recipe screen
					if (event.getNewScreen() instanceof IRecipesGui) {
						//If it is mark on our current screen that we are switching to JEI
						screen.switchingToJEI = true;
					}
				}
			});
		}

		evt.enqueueWork(() -> {
			//Property Overrides
			addPropertyOverrides(ACTIVE_OVERRIDE, (stack, level, entity, seed) -> ItemHelper.checkItemNBT(stack, Constants.NBT_KEY_ACTIVE) ? 1F : 0F,
					PEItems.GEM_OF_ETERNAL_DENSITY, PEItems.VOID_RING, PEItems.ARCANA_RING, PEItems.ARCHANGEL_SMITE, PEItems.BLACK_HOLE_BAND, PEItems.BODY_STONE,
					PEItems.HARVEST_GODDESS_BAND, PEItems.IGNITION_RING, PEItems.LIFE_STONE, PEItems.MIND_STONE, PEItems.SOUL_STONE, PEItems.WATCH_OF_FLOWING_TIME,
					PEItems.ZERO_RING);
			addPropertyOverrides(MODE_OVERRIDE, (stack, level, entity, seed) -> stack.hasTag() ? stack.getOrCreateTag().getInt(Constants.NBT_KEY_MODE) : 0F,
					PEItems.ARCANA_RING, PEItems.SWIFTWOLF_RENDING_GALE);
		});
	}

	@SubscribeEvent
	public static void registerKeybindings(RegisterKeyMappingsEvent event) {
		ClientKeyHelper.registerKeyBindings(event);
	}

	@SubscribeEvent
	public static void registerOverlays(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.CROSSHAIR.id(), PECore.rl("transmutation_result"), new TransmutationRenderingOverlay());
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		//Block Entity
		event.registerBlockEntityRenderer(PEBlockEntityTypes.ALCHEMICAL_CHEST.get(), context -> new ChestRenderer(context, PECore.rl("textures/block/alchemical_chest.png"), () -> PEBlocks.ALCHEMICAL_CHEST));
		event.registerBlockEntityRenderer(PEBlockEntityTypes.CONDENSER.get(), context -> new ChestRenderer(context, PECore.rl("textures/block/condenser_mk1.png"), () -> PEBlocks.CONDENSER));
		event.registerBlockEntityRenderer(PEBlockEntityTypes.CONDENSER_MK2.get(), context -> new ChestRenderer(context, PECore.rl("textures/block/condenser_mk2.png"), () -> PEBlocks.CONDENSER_MK2));
		event.registerBlockEntityRenderer(PEBlockEntityTypes.DARK_MATTER_PEDESTAL.get(), PedestalRenderer::new);

		//Entities
		event.registerEntityRenderer(PEEntityTypes.WATER_PROJECTILE.get(), context -> new EntitySpriteRenderer<>(context, PECore.rl("textures/entity/water_orb.png")));
		event.registerEntityRenderer(PEEntityTypes.LAVA_PROJECTILE.get(), context -> new EntitySpriteRenderer<>(context, PECore.rl("textures/entity/lava_orb.png")));
		event.registerEntityRenderer(PEEntityTypes.MOB_RANDOMIZER.get(), context -> new EntitySpriteRenderer<>(context, PECore.rl("textures/entity/randomizer.png")));
		event.registerEntityRenderer(PEEntityTypes.LENS_PROJECTILE.get(), context -> new EntitySpriteRenderer<>(context, PECore.rl("textures/entity/lens_explosive.png")));
		event.registerEntityRenderer(PEEntityTypes.FIRE_PROJECTILE.get(), context -> new EntitySpriteRenderer<>(context, PECore.rl("textures/entity/fireball.png")));
		event.registerEntityRenderer(PEEntityTypes.SWRG_PROJECTILE.get(), context -> new EntitySpriteRenderer<>(context, PECore.rl("textures/entity/lightning.png")));
		event.registerEntityRenderer(PEEntityTypes.NOVA_CATALYST_PRIMED.get(), context -> new NovaRenderer<>(context, PEBlocks.NOVA_CATALYST.getBlock()::defaultBlockState));
		event.registerEntityRenderer(PEEntityTypes.NOVA_CATACLYSM_PRIMED.get(), context -> new NovaRenderer<>(context, PEBlocks.NOVA_CATACLYSM.getBlock()::defaultBlockState));
		event.registerEntityRenderer(PEEntityTypes.HOMING_ARROW.get(), TippableArrowRenderer::new);
	}

	@SubscribeEvent
	public static void addLayers(EntityRenderersEvent.AddLayers event) {
		for (PlayerSkin.Model model : event.getSkins()) {
			PlayerRenderer skin = event.getSkin(model);
			if (skin != null) {
				skin.addLayer(new LayerYue(skin));
			}
		}
	}

	private static void addPropertyOverrides(ResourceLocation override, ItemPropertyFunction propertyGetter, ItemLike... itemProviders) {
		for (ItemLike itemProvider : itemProviders) {
			ItemProperties.register(itemProvider.asItem(), override, propertyGetter);
		}
	}
}