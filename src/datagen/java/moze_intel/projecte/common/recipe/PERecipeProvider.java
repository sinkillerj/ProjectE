package moze_intel.projecte.common.recipe;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;

public class PERecipeProvider extends RecipeProvider {

	public PERecipeProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
		fuelUpgradeRecipe(consumer, Items.COAL, PEItems.ALCHEMICAL_COAL);
		fuelUpgradeRecipe(consumer, PEItems.ALCHEMICAL_COAL, PEItems.MOBIUS_FUEL);
		fuelUpgradeRecipe(consumer, PEItems.MOBIUS_FUEL, PEItems.AETERNALIS_FUEL);
		fuelBlockRecipes(consumer, PEItems.ALCHEMICAL_COAL, PEBlocks.ALCHEMICAL_COAL);
		fuelBlockRecipes(consumer, PEItems.MOBIUS_FUEL, PEBlocks.MOBIUS_FUEL);
		fuelBlockRecipes(consumer, PEItems.AETERNALIS_FUEL, PEBlocks.AETERNALIS_FUEL);

		matterBlockRecipes(consumer, PEItems.DARK_MATTER, PEBlocks.DARK_MATTER);
		matterBlockRecipes(consumer, PEItems.RED_MATTER, PEBlocks.RED_MATTER);
		darkMatterGearRecipes(consumer);
		redMatterGearRecipes(consumer);
		gemArmorRecipes(consumer);

		addBagRecipes(consumer);
		//Conversion recipes
		addConversionRecipes(consumer);
	}

	private static void darkMatterGearRecipes(Consumer<IFinishedRecipe> consumer) {
		ICriterionInstance hasMatter = hasItem(PEItems.DARK_MATTER);
		//Helmet
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_HELMET)
				.patternLine("MMM")
				.patternLine("M M")
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
		//Chestplate
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_CHESTPLATE)
				.patternLine("M M")
				.patternLine("MMM")
				.patternLine("MMM")
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
		//Leggings
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_LEGGINGS)
				.patternLine("MMM")
				.patternLine("M M")
				.patternLine("M M")
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
		//Boots
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_BOOTS)
				.patternLine("M M")
				.patternLine("M M")
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
		//Axe
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_AXE)
				.patternLine("MM")
				.patternLine("MD")
				.patternLine(" D")
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
		//Pickaxe
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_PICKAXE)
				.patternLine("MMM")
				.patternLine(" D ")
				.patternLine(" D ")
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
		//Shovel
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_SHOVEL)
				.patternLine("M")
				.patternLine("D")
				.patternLine("D")
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
		//Sword
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_SWORD)
				.patternLine("M")
				.patternLine("M")
				.patternLine("D")
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
		//Hoe
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_HOE)
				.patternLine("MM")
				.patternLine(" D")
				.patternLine(" D")
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
		//Shears
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_SHEARS)
				.patternLine(" M")
				.patternLine("D ")
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
		//Hammer
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER_HAMMER)
				.patternLine("MDM")
				.patternLine(" D ")
				.patternLine(" D ")
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasMatter)
				.build(consumer);
	}

	private static void redMatterGearRecipes(Consumer<IFinishedRecipe> consumer) {
		//Helmet
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_HELMET)
				.patternLine("MMM")
				.patternLine("MDM")
				.key('M', PEItems.RED_MATTER)
				.key('D', PEItems.DARK_MATTER_HELMET)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_HELMET))
				.build(consumer);
		//Chestplate
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_CHESTPLATE)
				.patternLine("MDM")
				.patternLine("MMM")
				.patternLine("MMM")
				.key('M', PEItems.RED_MATTER)
				.key('D', PEItems.DARK_MATTER_CHESTPLATE)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_CHESTPLATE))
				.build(consumer);
		//Leggings
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_LEGGINGS)
				.patternLine("MMM")
				.patternLine("MDM")
				.patternLine("M M")
				.key('M', PEItems.RED_MATTER)
				.key('D', PEItems.DARK_MATTER_LEGGINGS)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_LEGGINGS))
				.build(consumer);
		//Boots
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_BOOTS)
				.patternLine("MDM")
				.patternLine("M M")
				.key('M', PEItems.RED_MATTER)
				.key('D', PEItems.DARK_MATTER_BOOTS)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_BOOTS))
				.build(consumer);
		//Axe
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_AXE)
				.patternLine("RR")
				.patternLine("RA")
				.patternLine(" M")
				.key('R', PEItems.RED_MATTER)
				.key('M', PEItems.DARK_MATTER)
				.key('A', PEItems.DARK_MATTER_AXE)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_AXE))
				.build(consumer);
		//Pickaxe
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_PICKAXE)
				.patternLine("RRR")
				.patternLine(" P ")
				.patternLine(" M ")
				.key('R', PEItems.RED_MATTER)
				.key('M', PEItems.DARK_MATTER)
				.key('P', PEItems.DARK_MATTER_PICKAXE)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_PICKAXE))
				.build(consumer);
		//Shovel
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_SHOVEL)
				.patternLine("R")
				.patternLine("S")
				.patternLine("M")
				.key('R', PEItems.RED_MATTER)
				.key('M', PEItems.DARK_MATTER)
				.key('S', PEItems.DARK_MATTER_SHOVEL)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_SHOVEL))
				.build(consumer);
		//Sword
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_SWORD)
				.patternLine("R")
				.patternLine("R")
				.patternLine("S")
				.key('R', PEItems.RED_MATTER)
				.key('S', PEItems.DARK_MATTER_SWORD)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_SWORD))
				.build(consumer);
		//Hoe
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_HOE)
				.patternLine("RR")
				.patternLine(" H")
				.patternLine(" M")
				.key('R', PEItems.RED_MATTER)
				.key('M', PEItems.DARK_MATTER)
				.key('H', PEItems.DARK_MATTER_HOE)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_HOE))
				.build(consumer);
		//Shears
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_SHEARS)
				.patternLine(" R")
				.patternLine("S ")
				.key('R', PEItems.RED_MATTER)
				.key('S', PEItems.DARK_MATTER_SHEARS)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_SHEARS))
				.build(consumer);
		//Hammer
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_HAMMER)
				.patternLine("RMR")
				.patternLine(" H ")
				.patternLine(" M ")
				.key('R', PEItems.RED_MATTER)
				.key('M', PEItems.DARK_MATTER)
				.key('H', PEItems.DARK_MATTER_HAMMER)
				.addCriterion("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_HAMMER))
				.build(consumer);
		//Katar (unlike the other recipes, any of the tools will work as a recipe unlock/showing)
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_KATAR)
				.patternLine("123")
				.patternLine("4RR")
				.patternLine("RRR")
				.key('1', PEItems.RED_MATTER_SHEARS)
				.key('2', PEItems.RED_MATTER_AXE)
				.key('3', PEItems.RED_MATTER_SWORD)
				.key('4', PEItems.RED_MATTER_HOE)
				.key('R', PEItems.RED_MATTER)
				.addCriterion("has_shears", hasItem(PEItems.RED_MATTER_SHEARS))
				.addCriterion("has_axe", hasItem(PEItems.RED_MATTER_AXE))
				.addCriterion("has_sword", hasItem(PEItems.RED_MATTER_SWORD))
				.addCriterion("has_hoe", hasItem(PEItems.RED_MATTER_HOE))
				.build(consumer);
		//Morning Star (unlike the other recipes, any of the tools will work as a recipe unlock/showing)
		ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER_MORNING_STAR)
				.patternLine("123")
				.patternLine("RRR")
				.patternLine("RRR")
				.key('1', PEItems.RED_MATTER_HAMMER)
				.key('2', PEItems.RED_MATTER_PICKAXE)
				.key('3', PEItems.RED_MATTER_SHOVEL)
				.key('R', PEItems.RED_MATTER)
				.addCriterion("has_hammer", hasItem(PEItems.RED_MATTER_HAMMER))
				.addCriterion("has_pickaxe", hasItem(PEItems.RED_MATTER_PICKAXE))
				.addCriterion("has_shovel", hasItem(PEItems.RED_MATTER_SHOVEL))
				.build(consumer);
	}

	private static void gemArmorRecipes(Consumer<IFinishedRecipe> consumer) {
		//Helmet
		ShapelessRecipeBuilder.shapelessRecipe(PEItems.GEM_HELMET)
				.addIngredient(PEItems.RED_MATTER_HELMET)
				.addIngredient(PEItems.KLEIN_STAR_OMEGA)
				.addIngredient(PEItems.EVERTIDE_AMULET)
				.addIngredient(PEItems.SOUL_STONE)
				.addCriterion("has_helmet", hasItem(PEItems.RED_MATTER_HELMET))
				.build(consumer);
		//Chestplate
		ShapelessRecipeBuilder.shapelessRecipe(PEItems.GEM_CHESTPLATE)
				.addIngredient(PEItems.RED_MATTER_CHESTPLATE)
				.addIngredient(PEItems.KLEIN_STAR_OMEGA)
				.addIngredient(PEItems.VOLCANITE_AMULET)
				.addIngredient(PEItems.BODY_STONE)
				.addCriterion("has_chestplate", hasItem(PEItems.RED_MATTER_CHESTPLATE))
				.build(consumer);
		//Leggings
		ShapelessRecipeBuilder.shapelessRecipe(PEItems.GEM_LEGGINGS)
				.addIngredient(PEItems.RED_MATTER_LEGGINGS)
				.addIngredient(PEItems.KLEIN_STAR_OMEGA)
				.addIngredient(PEItems.BLACK_HOLE_BAND)
				.addIngredient(PEItems.WATCH_OF_FLOWING_TIME)
				.addCriterion("has_leggings", hasItem(PEItems.RED_MATTER_LEGGINGS))
				.build(consumer);
		//Boots
		ShapelessRecipeBuilder.shapelessRecipe(PEItems.GEM_BOOTS)
				.addIngredient(PEItems.RED_MATTER_BOOTS)
				.addIngredient(PEItems.KLEIN_STAR_OMEGA)
				.addIngredient(PEItems.SWIFTWOLF_RENDING_GALE, 2)
				.addCriterion("has_boots", hasItem(PEItems.RED_MATTER_BOOTS))
				.build(consumer);
	}

	private static void fuelUpgradeRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output) {
		String inputName = getName(input);
		String outputName = getName(output);
		ShapelessRecipeBuilder.shapelessRecipe(output)
				.addIngredient(PEItems.PHILOSOPHERS_STONE)
				.addIngredient(input, 4)
				.addCriterion("has_" + inputName, hasItems(PEItems.PHILOSOPHERS_STONE, input))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(input, 4)
				.addIngredient(PEItems.PHILOSOPHERS_STONE)
				.addIngredient(output)
				.addCriterion("has_" + outputName, hasItems(PEItems.PHILOSOPHERS_STONE, output))
				.build(consumer, PECore.rl("conversions/" + outputName + "_to_" + inputName));
	}

	private static void fuelBlockRecipes(Consumer<IFinishedRecipe> consumer, IItemProvider fuel, IItemProvider block) {
		ShapedRecipeBuilder.shapedRecipe(block)
				.patternLine("FFF")
				.patternLine("FFF")
				.patternLine("FFF")
				.key('F', fuel)
				.addCriterion("has_" + getName(fuel), hasItem(fuel))
				.build(consumer);
		String blockName = getName(block);
		ShapelessRecipeBuilder.shapelessRecipe(fuel, 9)
				.addIngredient(block)
				.addCriterion("has_" + blockName, hasItem(block))
				.build(consumer, PECore.rl("conversions/" + blockName + "_deconstruct"));
	}

	private static void matterBlockRecipes(Consumer<IFinishedRecipe> consumer, IItemProvider matter, IItemProvider block) {
		ShapedRecipeBuilder.shapedRecipe(block)
				.patternLine("MM")
				.patternLine("MM")
				.key('M', matter)
				.addCriterion("has_" + getName(matter), hasItem(matter))
				.build(consumer);
		String blockName = getName(block);
		ShapelessRecipeBuilder.shapelessRecipe(matter, 4)
				.addIngredient(block)
				.addCriterion("has_" + blockName, hasItem(block))
				.build(consumer, PECore.rl("conversions/" + blockName + "_deconstruct"));
	}

	private static void addBagRecipes(Consumer<IFinishedRecipe> consumer) {
		ICriterionInstance hasChest = hasItem(PEBlocks.ALCHEMICAL_CHEST);
		ICriterionInstance hasBag = hasItem(PETags.Items.ALCHEMICAL_BAGS);
		for (DyeColor color : DyeColor.values()) {
			AlchemicalBag bag = PEItems.getBag(color);
			//Crafting recipe
			ShapedRecipeBuilder.shapedRecipe(bag)
					.patternLine("CCC")
					.patternLine("WAW")
					.patternLine("WWW")
					.key('A', PEBlocks.ALCHEMICAL_CHEST)
					.key('C', PEItems.HIGH_COVALENCE_DUST)
					.key('W', getWool(color))
					.addCriterion("has_alchemical_chest", hasChest)
					.build(consumer);
			//Dye bag conversion recipes
			ShapelessRecipeBuilder.shapelessRecipe(bag)
					.addIngredient(PETags.Items.ALCHEMICAL_BAGS)
					.addIngredient(color.getTag())
					.addCriterion("has_alchemical_bag", hasBag)
					.build(consumer, PECore.rl("conversions/dye_bag_" + color));
		}
	}

	private static IItemProvider getWool(DyeColor color) {
		switch (color) {
			default:
			case WHITE:
				return Items.WHITE_WOOL;
			case ORANGE:
				return Items.ORANGE_WOOL;
			case MAGENTA:
				return Items.MAGENTA_WOOL;
			case LIGHT_BLUE:
				return Items.LIGHT_BLUE_WOOL;
			case YELLOW:
				return Items.YELLOW_WOOL;
			case LIME:
				return Items.LIME_WOOL;
			case PINK:
				return Items.PINK_WOOL;
			case GRAY:
				return Items.GRAY_WOOL;
			case LIGHT_GRAY:
				return Items.LIGHT_GRAY_WOOL;
			case CYAN:
				return Items.CYAN_WOOL;
			case PURPLE:
				return Items.PURPLE_WOOL;
			case BLUE:
				return Items.BLUE_WOOL;
			case BROWN:
				return Items.BROWN_WOOL;
			case GREEN:
				return Items.GREEN_WOOL;
			case RED:
				return Items.RED_WOOL;
			case BLACK:
				return Items.BLACK_WOOL;
		}
	}

	private static void addConversionRecipes(Consumer<IFinishedRecipe> consumer) {
		philoConversionRecipe(consumer, Items.CHARCOAL, 4, Items.COAL, 1);
		philoConversionRecipe(consumer, Tags.Items.GEMS_DIAMOND, Items.DIAMOND, 2, Tags.Items.GEMS_EMERALD, Items.EMERALD, 1);
		philoConversionRecipe(consumer, Tags.Items.INGOTS_GOLD, Items.GOLD_INGOT, 4, Tags.Items.GEMS_DIAMOND, Items.DIAMOND, 1);
		philoConversionRecipe(consumer, Tags.Items.INGOTS_IRON, Items.IRON_INGOT, 8, Tags.Items.INGOTS_GOLD, Items.GOLD_INGOT, 1);
		//Iron -> Ender Pearl
		philoConversionRecipe(consumer, getName(Items.IRON_INGOT), Tags.Items.INGOTS_IRON, 4, getName(Items.ENDER_PEARL), Items.ENDER_PEARL, 1);
		//Dirt -> Grass
		ShapelessRecipeBuilder.shapelessRecipe(Items.GRASS_BLOCK)
				.addIngredient(PEItems.ARCANA_RING)
				.addIngredient(Items.DIRT)
				.addCriterion("has_arcana_ring", hasItem(PEItems.ARCANA_RING))
				.build(consumer, PECore.rl("conversions/dirt_to_grass"));
		//TODO - 1.16: Decide if we should also have a recipe to convert it for the harvest goddess band?
		// Would require changing it to a container item, which then would change if it persists in other recipes,
		// so may not be worth it, as we would need to have a custom recipe
		//Redstone -> Lava
		ShapelessRecipeBuilder.shapelessRecipe(Items.LAVA_BUCKET)
				.addIngredient(PEItems.VOLCANITE_AMULET)
				.addIngredient(Items.BUCKET)
				.addIngredient(Tags.Items.DUSTS_REDSTONE)
				.addCriterion("has_volcanite_amulet", hasItem(PEItems.VOLCANITE_AMULET))
				.build(consumer, PECore.rl("conversions/redstone_to_lava"));
		//Water -> Ice
		// Arcana
		ShapelessRecipeBuilder.shapelessRecipe(Items.ICE)
				.addIngredient(PEItems.ARCANA_RING)
				.addIngredient(Items.WATER_BUCKET)
				.addCriterion("has_arcana_ring", hasItem(PEItems.ARCANA_RING))
				.build(consumer, PECore.rl("conversions/water_to_ice_arcana"));
		// Zero
		ShapelessRecipeBuilder.shapelessRecipe(Items.ICE)
				.addIngredient(PEItems.ZERO_RING)
				.addIngredient(Items.WATER_BUCKET)
				.addCriterion("has_zero_ring", hasItem(PEItems.ZERO_RING))
				.build(consumer, PECore.rl("conversions/water_to_ice_zero"));
	}

	private static void philoConversionRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider a, int aAmount, IItemProvider b, int bAmount) {
		String aName = getName(a);
		String bName = getName(b);
		ShapelessRecipeBuilder.shapelessRecipe(b, bAmount)
				.addIngredient(PEItems.PHILOSOPHERS_STONE)
				.addIngredient(a, aAmount)
				.addCriterion("has_" + aName, hasItems(PEItems.PHILOSOPHERS_STONE, a))
				.build(consumer, PECore.rl("conversions/" + aName + "_to_" + bName));
		ShapelessRecipeBuilder.shapelessRecipe(a, aAmount)
				.addIngredient(PEItems.PHILOSOPHERS_STONE)
				.addIngredient(b, bAmount)
				.addCriterion("has_" + bName, hasItems(PEItems.PHILOSOPHERS_STONE, b))
				.build(consumer, PECore.rl("conversions/" + bName + "_to_" + aName));
	}

	private static void philoConversionRecipe(Consumer<IFinishedRecipe> consumer, ITag<Item> aTag, IItemProvider a, int aAmount, ITag<Item> bTag, IItemProvider b,
			int bAmount) {
		String aName = getName(a);
		String bName = getName(b);
		//A to B
		philoConversionRecipe(consumer, aName, aTag, aAmount, bName, b, bAmount);
		//B to A
		philoConversionRecipe(consumer, bName, bTag, bAmount, aName, a, aAmount);
	}

	private static void philoConversionRecipe(Consumer<IFinishedRecipe> consumer, String inputName, ITag<Item> inputTag, int inputAmount, String outputName,
			IItemProvider output, int outputAmount) {
		ShapelessRecipeBuilder bToA = ShapelessRecipeBuilder.shapelessRecipe(output, outputAmount)
				.addIngredient(PEItems.PHILOSOPHERS_STONE)
				.addCriterion("has_" + inputName, hasItems(PEItems.PHILOSOPHERS_STONE, inputTag));
		for (int i = 0; i < inputAmount; i++) {
			bToA.addIngredient(inputTag);
		}
		bToA.build(consumer, PECore.rl("conversions/" + inputName + "_to_" + outputName));
	}

	private static String getName(IItemProvider item) {
		return item.asItem().getRegistryName().getPath();
	}

	protected static InventoryChangeTrigger.Instance hasItems(IItemProvider... items) {
		return InventoryChangeTrigger.Instance.forItems(items);
	}

	@SafeVarargs
	protected static InventoryChangeTrigger.Instance hasItems(IItemProvider item, ITag<Item>... tags) {
		return hasItems(new IItemProvider[]{item}, tags);
	}

	@SafeVarargs
	protected static InventoryChangeTrigger.Instance hasItems(IItemProvider[] items, ITag<Item>... tags) {
		ItemPredicate[] predicates = new ItemPredicate[items.length + tags.length];
		for (int i = 0; i < items.length; ++i) {
			predicates[i] = ItemPredicate.Builder.create().item(items[i]).build();
		}
		for (int i = 0; i < tags.length; ++i) {
			predicates[items.length + i] = ItemPredicate.Builder.create().tag(tags[i]).build();
		}
		return hasItem(predicates);
	}
}