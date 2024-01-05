package moze_intel.projecte.gameObjs.registries;

import java.util.function.Consumer;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.impl.CreativeTabDeferredRegister;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class PECreativeTabs {

	public static final CreativeTabDeferredRegister CREATIVE_TABS = new CreativeTabDeferredRegister(PECore.MODID, PECreativeTabs::addToExistingTabs);

	public static final PEDeferredHolder<CreativeModeTab, CreativeModeTab> PROJECTE = CREATIVE_TABS.registerMain(PELang.PROJECTE, PEItems.PHILOSOPHERS_STONE, builder ->
			builder.displayItems((displayParameters, output) -> {
				output.accept(PEItems.PHILOSOPHERS_STONE);
				output.accept(PEItems.REPAIR_TALISMAN);
				output.accept(PEItems.TOME_OF_KNOWLEDGE);
				output.accept(PEItems.TRANSMUTATION_TABLET);
				output.accept(PEBlocks.TRANSMUTATION_TABLE);

				output.accept(PEItems.LOW_COVALENCE_DUST);
				output.accept(PEItems.MEDIUM_COVALENCE_DUST);
				output.accept(PEItems.HIGH_COVALENCE_DUST);

				output.accept(PEItems.ALCHEMICAL_COAL);
				output.accept(PEItems.MOBIUS_FUEL);
				output.accept(PEItems.AETERNALIS_FUEL);
				output.accept(PEItems.DARK_MATTER);
				output.accept(PEItems.RED_MATTER);
				output.accept(PEBlocks.ALCHEMICAL_COAL);
				output.accept(PEBlocks.MOBIUS_FUEL);
				output.accept(PEBlocks.AETERNALIS_FUEL);
				output.accept(PEBlocks.DARK_MATTER);
				output.accept(PEBlocks.RED_MATTER);

				output.accept(PEItems.KLEIN_STAR_EIN);
				output.accept(PEItems.KLEIN_STAR_ZWEI);
				output.accept(PEItems.KLEIN_STAR_DREI);
				output.accept(PEItems.KLEIN_STAR_VIER);
				output.accept(PEItems.KLEIN_STAR_SPHERE);
				output.accept(PEItems.KLEIN_STAR_OMEGA);

				output.accept(PEItems.DARK_MATTER_PICKAXE);
				output.accept(PEItems.DARK_MATTER_AXE);
				output.accept(PEItems.DARK_MATTER_SHOVEL);
				output.accept(PEItems.DARK_MATTER_HOE);
				output.accept(PEItems.DARK_MATTER_SHEARS);
				output.accept(PEItems.DARK_MATTER_HAMMER);
				output.accept(PEItems.DARK_MATTER_SWORD);

				output.accept(PEItems.RED_MATTER_PICKAXE);
				output.accept(PEItems.RED_MATTER_AXE);
				output.accept(PEItems.RED_MATTER_SHOVEL);
				output.accept(PEItems.RED_MATTER_HOE);
				output.accept(PEItems.RED_MATTER_SHEARS);
				output.accept(PEItems.RED_MATTER_HAMMER);
				output.accept(PEItems.RED_MATTER_MORNING_STAR);
				output.accept(PEItems.RED_MATTER_SWORD);
				output.accept(PEItems.RED_MATTER_KATAR);

				addArmor(output::accept);

				output.accept(PEItems.DESTRUCTION_CATALYST);
				output.accept(PEItems.HYPERKINETIC_LENS);
				output.accept(PEItems.CATALYTIC_LENS);

				output.accept(PEItems.IRON_BAND);
				output.accept(PEItems.BLACK_HOLE_BAND);
				output.accept(PEItems.ARCHANGEL_SMITE);
				output.accept(PEItems.HARVEST_GODDESS_BAND);
				output.accept(PEItems.IGNITION_RING);
				output.accept(PEItems.ZERO_RING);
				output.accept(PEItems.SWIFTWOLF_RENDING_GALE);
				output.accept(PEItems.WATCH_OF_FLOWING_TIME);
				output.accept(PEItems.EVERTIDE_AMULET);
				output.accept(PEItems.VOLCANITE_AMULET);
				output.accept(PEItems.GEM_OF_ETERNAL_DENSITY);
				output.accept(PEItems.MERCURIAL_EYE);
				output.accept(PEItems.VOID_RING);

				for (byte i = 0; i < PEItems.ARCANA_RING.asItem().getModeCount(); ++i) {
					ItemStack stack = PEItems.ARCANA_RING.asStack();
					stack.getOrCreateTag().putByte(Constants.NBT_KEY_MODE, i);
					output.accept(stack);
				}

				output.accept(PEItems.BODY_STONE);
				output.accept(PEItems.SOUL_STONE);
				output.accept(PEItems.MIND_STONE);
				output.accept(PEItems.LIFE_STONE);

				output.accept(PEItems.LOW_DIVINING_ROD);
				output.accept(PEItems.MEDIUM_DIVINING_ROD);
				output.accept(PEItems.HIGH_DIVINING_ROD);

				output.accept(PEItems.WHITE_ALCHEMICAL_BAG);
				output.accept(PEItems.ORANGE_ALCHEMICAL_BAG);
				output.accept(PEItems.MAGENTA_ALCHEMICAL_BAG);
				output.accept(PEItems.LIGHT_BLUE_ALCHEMICAL_BAG);
				output.accept(PEItems.YELLOW_ALCHEMICAL_BAG);
				output.accept(PEItems.LIME_ALCHEMICAL_BAG);
				output.accept(PEItems.PINK_ALCHEMICAL_BAG);
				output.accept(PEItems.GRAY_ALCHEMICAL_BAG);
				output.accept(PEItems.LIGHT_GRAY_ALCHEMICAL_BAG);
				output.accept(PEItems.CYAN_ALCHEMICAL_BAG);
				output.accept(PEItems.PURPLE_ALCHEMICAL_BAG);
				output.accept(PEItems.BLUE_ALCHEMICAL_BAG);
				output.accept(PEItems.BROWN_ALCHEMICAL_BAG);
				output.accept(PEItems.GREEN_ALCHEMICAL_BAG);
				output.accept(PEItems.RED_ALCHEMICAL_BAG);
				output.accept(PEItems.BLACK_ALCHEMICAL_BAG);

				output.accept(PEBlocks.ALCHEMICAL_CHEST);
				output.accept(PEBlocks.CONDENSER);
				output.accept(PEBlocks.CONDENSER_MK2);

				output.accept(PEBlocks.COLLECTOR);
				output.accept(PEBlocks.COLLECTOR_MK2);
				output.accept(PEBlocks.COLLECTOR_MK3);
				output.accept(PEBlocks.RELAY);
				output.accept(PEBlocks.RELAY_MK2);
				output.accept(PEBlocks.RELAY_MK3);

				output.accept(PEBlocks.DARK_MATTER_PEDESTAL);
				output.accept(PEBlocks.DARK_MATTER_FURNACE);
				output.accept(PEBlocks.RED_MATTER_FURNACE);
				output.accept(PEBlocks.INTERDICTION_TORCH);
				output.accept(PEBlocks.NOVA_CATALYST);
				output.accept(PEBlocks.NOVA_CATACLYSM);
			})
	);

	private static void addArmor(Consumer<ItemLike> output) {
		output.accept(PEItems.DARK_MATTER_HELMET);
		output.accept(PEItems.DARK_MATTER_CHESTPLATE);
		output.accept(PEItems.DARK_MATTER_LEGGINGS);
		output.accept(PEItems.DARK_MATTER_BOOTS);

		output.accept(PEItems.RED_MATTER_HELMET);
		output.accept(PEItems.RED_MATTER_CHESTPLATE);
		output.accept(PEItems.RED_MATTER_LEGGINGS);
		output.accept(PEItems.RED_MATTER_BOOTS);

		output.accept(PEItems.GEM_HELMET);
		output.accept(PEItems.GEM_CHESTPLATE);
		output.accept(PEItems.GEM_LEGGINGS);
		output.accept(PEItems.GEM_BOOTS);
	}

	private static void addToExistingTabs(BuildCreativeModeTabContentsEvent event) {
		ResourceKey<CreativeModeTab> tabKey = event.getTabKey();
		if (tabKey == CreativeModeTabs.BUILDING_BLOCKS) {
			addToExistingTab(event,
					PEBlocks.ALCHEMICAL_COAL,
					PEBlocks.MOBIUS_FUEL,
					PEBlocks.AETERNALIS_FUEL,
					PEBlocks.DARK_MATTER,
					PEBlocks.RED_MATTER
			);
		} else if (tabKey == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
			addToExistingTab(event,
					PEBlocks.INTERDICTION_TORCH,
					PEBlocks.TRANSMUTATION_TABLE,

					PEBlocks.ALCHEMICAL_CHEST,
					PEBlocks.CONDENSER,
					PEBlocks.CONDENSER_MK2,

					PEBlocks.COLLECTOR,
					PEBlocks.COLLECTOR_MK2,
					PEBlocks.COLLECTOR_MK3,
					PEBlocks.RELAY,
					PEBlocks.RELAY_MK2,
					PEBlocks.RELAY_MK3,

					PEBlocks.DARK_MATTER_PEDESTAL,

					PEBlocks.DARK_MATTER_FURNACE,
					PEBlocks.RED_MATTER_FURNACE
			);
		} else if (tabKey == CreativeModeTabs.REDSTONE_BLOCKS) {
			addToExistingTab(event,
					//Comparator supporting blocks
					PEBlocks.ALCHEMICAL_CHEST,
					PEBlocks.CONDENSER,
					PEBlocks.CONDENSER_MK2,

					PEBlocks.COLLECTOR,
					PEBlocks.COLLECTOR_MK2,
					PEBlocks.COLLECTOR_MK3,
					PEBlocks.RELAY,
					PEBlocks.RELAY_MK2,
					PEBlocks.RELAY_MK3,

					PEBlocks.DARK_MATTER_PEDESTAL,

					PEBlocks.DARK_MATTER_FURNACE,
					PEBlocks.RED_MATTER_FURNACE,
					//TNT like blocks
					PEBlocks.NOVA_CATALYST,
					PEBlocks.NOVA_CATACLYSM
			);
		} else if (tabKey == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			addToExistingTab(event,
					PEItems.DARK_MATTER_PICKAXE,
					PEItems.DARK_MATTER_AXE,
					PEItems.DARK_MATTER_SHOVEL,
					PEItems.DARK_MATTER_HOE,
					PEItems.DARK_MATTER_SHEARS,
					PEItems.DARK_MATTER_HAMMER,

					PEItems.RED_MATTER_PICKAXE,
					PEItems.RED_MATTER_AXE,
					PEItems.RED_MATTER_SHOVEL,
					PEItems.RED_MATTER_HOE,
					PEItems.RED_MATTER_SHEARS,
					PEItems.RED_MATTER_HAMMER,
					PEItems.RED_MATTER_MORNING_STAR
			);

			addToExistingTab(event,
					PEItems.PHILOSOPHERS_STONE,
					PEItems.REPAIR_TALISMAN,
					PEItems.TOME_OF_KNOWLEDGE,
					PEItems.TRANSMUTATION_TABLET,

					PEItems.DESTRUCTION_CATALYST,

					PEItems.BLACK_HOLE_BAND,
					PEItems.HARVEST_GODDESS_BAND,
					PEItems.IGNITION_RING,
					PEItems.ZERO_RING,
					PEItems.SWIFTWOLF_RENDING_GALE,
					PEItems.WATCH_OF_FLOWING_TIME,
					PEItems.EVERTIDE_AMULET,
					PEItems.VOLCANITE_AMULET,
					PEItems.GEM_OF_ETERNAL_DENSITY,
					PEItems.MERCURIAL_EYE,
					PEItems.VOID_RING
			);

			for (byte i = 0; i < PEItems.ARCANA_RING.asItem().getModeCount(); ++i) {
				ItemStack stack = PEItems.ARCANA_RING.asStack();
				stack.getOrCreateTag().putByte(Constants.NBT_KEY_MODE, i);
				event.accept(stack);
			}

			addToExistingTab(event,
					PEItems.BODY_STONE,
					PEItems.SOUL_STONE,
					PEItems.MIND_STONE,
					PEItems.LIFE_STONE,

					PEItems.LOW_DIVINING_ROD,
					PEItems.MEDIUM_DIVINING_ROD,
					PEItems.HIGH_DIVINING_ROD
			);

			addToExistingTab(event,
					PEItems.KLEIN_STAR_EIN,
					PEItems.KLEIN_STAR_ZWEI,
					PEItems.KLEIN_STAR_DREI,
					PEItems.KLEIN_STAR_VIER,
					PEItems.KLEIN_STAR_SPHERE,
					PEItems.KLEIN_STAR_OMEGA
			);

			addToExistingTab(event,
					PEItems.WHITE_ALCHEMICAL_BAG,
					PEItems.ORANGE_ALCHEMICAL_BAG,
					PEItems.MAGENTA_ALCHEMICAL_BAG,
					PEItems.LIGHT_BLUE_ALCHEMICAL_BAG,
					PEItems.YELLOW_ALCHEMICAL_BAG,
					PEItems.LIME_ALCHEMICAL_BAG,
					PEItems.PINK_ALCHEMICAL_BAG,
					PEItems.GRAY_ALCHEMICAL_BAG,
					PEItems.LIGHT_GRAY_ALCHEMICAL_BAG,
					PEItems.CYAN_ALCHEMICAL_BAG,
					PEItems.PURPLE_ALCHEMICAL_BAG,
					PEItems.BLUE_ALCHEMICAL_BAG,
					PEItems.BROWN_ALCHEMICAL_BAG,
					PEItems.GREEN_ALCHEMICAL_BAG,
					PEItems.RED_ALCHEMICAL_BAG,
					PEItems.BLACK_ALCHEMICAL_BAG
			);
		} else if (tabKey == CreativeModeTabs.COMBAT) {
			addToExistingTab(event,
					PEItems.DARK_MATTER_SWORD,
					PEItems.RED_MATTER_SWORD,
					PEItems.RED_MATTER_KATAR
			);

			addArmor(event::accept);

			addToExistingTab(event,
					PEItems.ARCHANGEL_SMITE,

					//These are not necessarily combat only, but it makes more sense to put them here than in tools
					PEItems.HYPERKINETIC_LENS,
					PEItems.CATALYTIC_LENS
			);
		} else if (tabKey == CreativeModeTabs.INGREDIENTS) {
			addToExistingTab(event,
					PEItems.LOW_COVALENCE_DUST,
					PEItems.MEDIUM_COVALENCE_DUST,
					PEItems.HIGH_COVALENCE_DUST,

					PEItems.ALCHEMICAL_COAL,
					PEItems.MOBIUS_FUEL,
					PEItems.AETERNALIS_FUEL,
					PEItems.DARK_MATTER,
					PEItems.RED_MATTER,

					PEItems.IRON_BAND
			);
		}
	}

	private static void addToExistingTab(BuildCreativeModeTabContentsEvent event, ItemLike... items) {
		for (ItemLike item : items) {
			event.accept(item);
		}
	}
}