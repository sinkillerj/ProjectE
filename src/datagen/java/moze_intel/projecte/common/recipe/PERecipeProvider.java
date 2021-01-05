package moze_intel.projecte.common.recipe;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.config.TomeEnabledCondition;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;

//TODO - 1.16: Make it so that we can have another recipe condition that toggles if the klein stars need to be filled for recipes or not
public class PERecipeProvider extends RecipeProvider {

	public PERecipeProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
		addCustomRecipeSerializer(consumer, PERecipeSerializers.COVALENCE_REPAIR.get());
		addCustomRecipeSerializer(consumer, PERecipeSerializers.PHILO_STONE_SMELTING.get());
		fuelUpgradeRecipe(consumer, Items.COAL, PEItems.ALCHEMICAL_COAL);
		fuelUpgradeRecipe(consumer, PEItems.ALCHEMICAL_COAL, PEItems.MOBIUS_FUEL);
		fuelUpgradeRecipe(consumer, PEItems.MOBIUS_FUEL, PEItems.AETERNALIS_FUEL);
		fuelBlockRecipes(consumer, PEItems.ALCHEMICAL_COAL, PEBlocks.ALCHEMICAL_COAL);
		fuelBlockRecipes(consumer, PEItems.MOBIUS_FUEL, PEBlocks.MOBIUS_FUEL);
		fuelBlockRecipes(consumer, PEItems.AETERNALIS_FUEL, PEBlocks.AETERNALIS_FUEL);
		addMatterRecipes(consumer);
		addBagRecipes(consumer);
		addCollectorRecipes(consumer);
		addRelayRecipes(consumer);
		addCondenserRecipes(consumer);
		addTransmutationTableRecipes(consumer);
		addNovaRecipes(consumer);
		addKleinRecipes(consumer);
		addRingRecipes(consumer);
		addCovalenceDustRecipes(consumer);
		addDiviningRodRecipes(consumer);
		addMiscToolRecipes(consumer);
		//Conversion recipes
		addConversionRecipes(consumer);
		//Alchemical Chest
		ShapedRecipeBuilder.shapedRecipe(PEBlocks.ALCHEMICAL_CHEST)
				.patternLine("LMH")
				.patternLine("SDS")
				.patternLine("ICI")
				.key('L', PEItems.LOW_COVALENCE_DUST)
				.key('M', PEItems.MEDIUM_COVALENCE_DUST)
				.key('H', PEItems.HIGH_COVALENCE_DUST)
				.key('S', Tags.Items.STONE)
				.key('I', Tags.Items.INGOTS_IRON)
				.key('C', Tags.Items.CHESTS_WOODEN)
				.key('D', Tags.Items.GEMS_DIAMOND)
				.addCriterion("has_covalence_dust", hasItems(PEItems.LOW_COVALENCE_DUST, PEItems.MEDIUM_COVALENCE_DUST, PEItems.HIGH_COVALENCE_DUST))
				.build(consumer);
		//Interdiction Torch
		ShapedRecipeBuilder.shapedRecipe(PEBlocks.INTERDICTION_TORCH)
				.patternLine("RDR")
				.patternLine("DPD")
				.patternLine("GGG")
				.key('R', Items.REDSTONE_TORCH)
				.key('G', Tags.Items.DUSTS_GLOWSTONE)
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('P', PEItems.PHILOSOPHERS_STONE)
				.addCriterion("has_philo_stone", hasItem(PEItems.PHILOSOPHERS_STONE))
				.build(consumer);
		//Tome of Knowledge
		ICondition tomeEnabledCondition = new TomeEnabledCondition();
		Consumer<IFinishedRecipe> tomeEnabledConsumer = recipe -> consumer.accept(new ConditionWrappedRecipeResult(recipe, tomeEnabledCondition));
		tomeRecipe(tomeEnabledConsumer, false);
		tomeRecipe(tomeEnabledConsumer, true);
	}

	private static void addCustomRecipeSerializer(Consumer<IFinishedRecipe> consumer, SpecialRecipeSerializer<?> serializer) {
		CustomRecipeBuilder.customRecipe(serializer).build(consumer, serializer.getRegistryName().toString());
	}

	private static void tomeRecipe(Consumer<IFinishedRecipe> consumer, boolean alternate) {
		String lowToHigh = "LMH";
		String highToLow = "HML";
		String name = PEItems.TOME_OF_KNOWLEDGE.get().getRegistryName().toString();
		ShapedRecipeBuilder tome = ShapedRecipeBuilder.shapedRecipe(PEItems.TOME_OF_KNOWLEDGE)
				.patternLine(alternate ? lowToHigh : highToLow)
				.patternLine("KBK")
				.patternLine(alternate ? highToLow : lowToHigh)
				.key('B', Items.BOOK)
				.key('K', PEItems.KLEIN_STAR_OMEGA)
				.key('L', PEItems.LOW_COVALENCE_DUST)
				.key('M', PEItems.MEDIUM_COVALENCE_DUST)
				.key('H', PEItems.HIGH_COVALENCE_DUST)
				.addCriterion("has_covalence_dust", hasItems(PEItems.LOW_COVALENCE_DUST, PEItems.MEDIUM_COVALENCE_DUST, PEItems.HIGH_COVALENCE_DUST))
				.setGroup(name);
		if (alternate) {
			tome.build(consumer, name + "_alt");
		} else {
			tome.build(consumer);
		}
	}

	private static void addMatterRecipes(Consumer<IFinishedRecipe> consumer) {
		matterBlockRecipes(consumer, PEItems.DARK_MATTER, PEBlocks.DARK_MATTER);
		matterBlockRecipes(consumer, PEItems.RED_MATTER, PEBlocks.RED_MATTER);
		darkMatterGearRecipes(consumer);
		redMatterGearRecipes(consumer);
		gemArmorRecipes(consumer);
		addFurnaceRecipes(consumer);
		//Dark Matter
		ShapedRecipeBuilder.shapedRecipe(PEItems.DARK_MATTER)
				.patternLine("AAA")
				.patternLine("ADA")
				.patternLine("AAA")
				.key('A', PEItems.AETERNALIS_FUEL)
				.key('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.addCriterion("has_aeternalis", hasItem(PEItems.AETERNALIS_FUEL))
				.build(consumer);
		//Dark Matter Pedestal
		ShapedRecipeBuilder.shapedRecipe(PEBlocks.DARK_MATTER_PEDESTAL)
				.patternLine("RDR")
				.patternLine("RDR")
				.patternLine("DDD")
				.key('R', PEItems.RED_MATTER)
				.key('D', PEBlocks.DARK_MATTER)
				.addCriterion("has_matter", hasItem(PEItems.RED_MATTER))
				.build(consumer);
		//Red Matter
		redMatterRecipe(consumer, false);
		redMatterRecipe(consumer, true);
	}

	private static void redMatterRecipe(Consumer<IFinishedRecipe> consumer, boolean alternate) {
		String name = PEItems.RED_MATTER.get().getRegistryName().toString();
		ShapedRecipeBuilder redMatter = ShapedRecipeBuilder.shapedRecipe(PEItems.RED_MATTER)
				.key('A', PEItems.AETERNALIS_FUEL)
				.key('D', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.setGroup(name);
		if (alternate) {
			redMatter.patternLine("ADA")
					.patternLine("ADA")
					.patternLine("ADA")
					.build(consumer, name + "_alt");
		} else {
			redMatter.patternLine("AAA")
					.patternLine("DDD")
					.patternLine("AAA")
					.build(consumer);
		}
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

	private static void addCollectorRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(PEBlocks.COLLECTOR)
				.patternLine("GTG")
				.patternLine("GDG")
				.patternLine("GFG")
				.key('G', Items.GLOWSTONE)
				.key('F', Items.FURNACE)
				.key('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.key('T', Items.GLASS)
				.addCriterion("has_diamond", hasItem(Tags.Items.STORAGE_BLOCKS_DIAMOND))
				.build(consumer);
		addCollectorUpgradeRecipes(consumer, PEBlocks.COLLECTOR_MK2, PEBlocks.COLLECTOR, PEItems.DARK_MATTER);
		addCollectorUpgradeRecipes(consumer, PEBlocks.COLLECTOR_MK3, PEBlocks.COLLECTOR_MK2, PEItems.RED_MATTER);
	}

	private static void addCollectorUpgradeRecipes(Consumer<IFinishedRecipe> consumer, IItemProvider collector, IItemProvider previous, IItemProvider upgradeItem) {
		ShapedRecipeBuilder.shapedRecipe(collector)
				.patternLine("GUG")
				.patternLine("GPG")
				.patternLine("GGG")
				.key('G', Items.GLOWSTONE)
				.key('P', previous)
				.key('U', upgradeItem)
				.addCriterion("has_previous", hasItem(previous))
				.build(consumer);
	}

	private static void addRelayRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(PEBlocks.RELAY)
				.patternLine("OSO")
				.patternLine("ODO")
				.patternLine("OOO")
				.key('S', Items.GLASS)
				.key('O', Tags.Items.OBSIDIAN)
				.key('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.addCriterion("has_diamond", hasItem(Tags.Items.STORAGE_BLOCKS_DIAMOND))
				.build(consumer);
		addRelayUpgradeRecipes(consumer, PEBlocks.RELAY_MK2, PEBlocks.RELAY, PEItems.DARK_MATTER);
		addRelayUpgradeRecipes(consumer, PEBlocks.RELAY_MK3, PEBlocks.RELAY_MK2, PEItems.RED_MATTER);
	}

	private static void addRelayUpgradeRecipes(Consumer<IFinishedRecipe> consumer, IItemProvider relay, IItemProvider previous, IItemProvider upgradeItem) {
		ShapedRecipeBuilder.shapedRecipe(relay)
				.patternLine("OUO")
				.patternLine("OPO")
				.patternLine("OOO")
				.key('O', Tags.Items.OBSIDIAN)
				.key('P', previous)
				.key('U', upgradeItem)
				.addCriterion("has_previous", hasItem(previous))
				.build(consumer);
	}

	private static void addCondenserRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(PEBlocks.CONDENSER)
				.patternLine("ODO")
				.patternLine("DCD")
				.patternLine("ODO")
				.key('C', PEBlocks.ALCHEMICAL_CHEST)
				.key('O', Tags.Items.OBSIDIAN)
				.key('D', Tags.Items.GEMS_DIAMOND)
				.addCriterion("has_alchemical_chest", hasItem(PEBlocks.ALCHEMICAL_CHEST))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(PEBlocks.CONDENSER_MK2)
				.patternLine("RDR")
				.patternLine("DCD")
				.patternLine("RDR")
				.key('R', PEBlocks.RED_MATTER)
				.key('C', PEBlocks.CONDENSER)
				.key('D', PEBlocks.DARK_MATTER)
				.addCriterion("has_previous", hasItem(PEBlocks.CONDENSER))
				.build(consumer);
	}

	private static void addFurnaceRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(PEBlocks.DARK_MATTER_FURNACE)
				.patternLine("DDD")
				.patternLine("DFD")
				.patternLine("DDD")
				.key('D', PEBlocks.DARK_MATTER)
				.key('F', Items.FURNACE)
				.addCriterion("has_dark_matter", hasItem(PEBlocks.DARK_MATTER))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(PEBlocks.RED_MATTER_FURNACE)
				.patternLine(" R ")
				.patternLine("RFR")
				.key('R', PEBlocks.RED_MATTER)
				.key('F', PEBlocks.DARK_MATTER_FURNACE)
				.addCriterion("has_previous", hasItem(PEBlocks.DARK_MATTER_FURNACE))
				.build(consumer);
	}

	private static void addKleinRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(PEItems.KLEIN_STAR_EIN)
				.patternLine("MMM")
				.patternLine("MDM")
				.patternLine("MMM")
				.key('M', PEItems.MOBIUS_FUEL)
				.key('D', Tags.Items.GEMS_DIAMOND)
				.addCriterion("has_components", hasItems(PEItems.MOBIUS_FUEL, Tags.Items.GEMS_DIAMOND))
				.build(consumer);
		kleinStarUpgrade(consumer, PEItems.KLEIN_STAR_ZWEI, PEItems.KLEIN_STAR_EIN);
		kleinStarUpgrade(consumer, PEItems.KLEIN_STAR_DREI, PEItems.KLEIN_STAR_ZWEI);
		kleinStarUpgrade(consumer, PEItems.KLEIN_STAR_VIER, PEItems.KLEIN_STAR_DREI);
		kleinStarUpgrade(consumer, PEItems.KLEIN_STAR_SPHERE, PEItems.KLEIN_STAR_VIER);
		kleinStarUpgrade(consumer, PEItems.KLEIN_STAR_OMEGA, PEItems.KLEIN_STAR_SPHERE);
	}

	private static void kleinStarUpgrade(Consumer<IFinishedRecipe> consumer, IItemProvider star, IItemProvider previous) {
		//Wrap the consumer so that we can replace it with the proper serializer
		ShapelessRecipeBuilder.shapelessRecipe(star)
				.addIngredient(previous, 4)
				.addCriterion("has_components", hasItem(previous))
				.build(recipe -> consumer.accept(new ShapelessKleinStarRecipeResult(recipe)));
	}

	private static void addRingRecipes(Consumer<IFinishedRecipe> consumer) {
		//Arcana (Any ring or red matter)
		ShapedRecipeBuilder.shapedRecipe(PEItems.ARCANA_RING)
				.patternLine("ZIH")
				.patternLine("SMM")
				.patternLine("MMM")
				.key('S', PEItems.SWIFTWOLF_RENDING_GALE)
				.key('H', PEItems.HARVEST_GODDESS_BAND)
				.key('I', PEItems.IGNITION_RING)
				.key('Z', PEItems.ZERO_RING)
				.key('M', PEItems.RED_MATTER)
				.addCriterion("has_swrg", hasItem(PEItems.SWIFTWOLF_RENDING_GALE))
				.addCriterion("has_harvest", hasItem(PEItems.HARVEST_GODDESS_BAND))
				.addCriterion("has_ignition", hasItem(PEItems.IGNITION_RING))
				.addCriterion("has_zero", hasItem(PEItems.ZERO_RING))
				.addCriterion("has_matter", hasItem(PEItems.RED_MATTER))
				.build(consumer);
		//Archangel Smite
		ShapedRecipeBuilder.shapedRecipe(PEItems.ARCHANGEL_SMITE)
				.patternLine("BFB")
				.patternLine("MIM")
				.patternLine("BFB")
				.key('B', Items.BOW)
				.key('F', Tags.Items.FEATHERS)
				.key('I', PEItems.IRON_BAND)
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.build(consumer);
		//Black Hole Band
		ShapedRecipeBuilder.shapedRecipe(PEItems.BLACK_HOLE_BAND)
				.patternLine("SSS")
				.patternLine("DID")
				.patternLine("SSS")
				.key('S', Tags.Items.STRING)
				.key('D', PEItems.DARK_MATTER)
				.key('I', PEItems.IRON_BAND)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.build(consumer);
		//Body Stone
		ShapedRecipeBuilder.shapedRecipe(PEItems.BODY_STONE)
				.patternLine("SSS")
				.patternLine("RLR")
				.patternLine("SSS")
				.key('R', PEItems.RED_MATTER)
				.key('S', Items.SUGAR)
				.key('L', Tags.Items.GEMS_LAPIS)
				.addCriterion("has_matter", hasItem(PEItems.RED_MATTER))
				.build(consumer);
		//Harvest Goddess
		ShapedRecipeBuilder.shapedRecipe(PEItems.HARVEST_GODDESS_BAND)
				.patternLine("SFS")
				.patternLine("DID")
				.patternLine("SFS")
				.key('S', ItemTags.SAPLINGS)
				.key('D', PEItems.DARK_MATTER)
				.key('F', Items.POPPY)
				.key('I', PEItems.IRON_BAND)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.build(consumer);
		//Ignition
		ShapedRecipeBuilder.shapedRecipe(PEItems.IGNITION_RING)
				.patternLine("FMF")
				.patternLine("DID")
				.patternLine("FMF")
				.key('D', PEItems.DARK_MATTER)
				.key('F', Items.FLINT_AND_STEEL)
				.key('I', PEItems.IRON_BAND)
				.key('M', PEItems.MOBIUS_FUEL)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.build(consumer);
		//Iron Band
		ShapedRecipeBuilder.shapedRecipe(PEItems.IRON_BAND)
				.patternLine("III")
				.patternLine("ILI")
				.patternLine("III")
				.key('I', Tags.Items.INGOTS_IRON)
				.key('L', Ingredient.fromItems(Items.LAVA_BUCKET, PEItems.VOLCANITE_AMULET))
				.addCriterion("has_lava", hasItem(Items.LAVA_BUCKET))
				.addCriterion("has_amulet", hasItem(PEItems.VOLCANITE_AMULET))
				.build(consumer);
		//Life Stone
		ShapelessRecipeBuilder.shapelessRecipe(PEItems.LIFE_STONE)
				.addIngredient(PEItems.BODY_STONE)
				.addIngredient(PEItems.SOUL_STONE)
				.addCriterion("has_body", hasItem(PEItems.BODY_STONE))
				.addCriterion("has_soul", hasItem(PEItems.SOUL_STONE))
				.build(consumer);
		//Mind Stone
		ShapedRecipeBuilder.shapedRecipe(PEItems.MIND_STONE)
				.patternLine("BBB")
				.patternLine("RLR")
				.patternLine("BBB")
				.key('R', PEItems.RED_MATTER)
				.key('B', Items.BOOK)
				.key('L', Tags.Items.GEMS_LAPIS)
				.addCriterion("has_matter", hasItem(PEItems.RED_MATTER))
				.build(consumer);
		//Soul Stone
		ShapedRecipeBuilder.shapedRecipe(PEItems.SOUL_STONE)
				.patternLine("GGG")
				.patternLine("RLR")
				.patternLine("GGG")
				.key('R', PEItems.RED_MATTER)
				.key('G', Tags.Items.DUSTS_GLOWSTONE)
				.key('L', Tags.Items.GEMS_LAPIS)
				.addCriterion("has_matter", hasItem(PEItems.RED_MATTER))
				.build(consumer);
		//SWRG
		ShapedRecipeBuilder.shapedRecipe(PEItems.SWIFTWOLF_RENDING_GALE)
				.patternLine("DFD")
				.patternLine("FIF")
				.patternLine("DFD")
				.key('D', PEItems.DARK_MATTER)
				.key('F', Tags.Items.FEATHERS)
				.key('I', PEItems.IRON_BAND)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.build(consumer);
		//Void Ring
		ShapelessRecipeBuilder.shapelessRecipe(PEItems.VOID_RING)
				.addIngredient(PEItems.BLACK_HOLE_BAND)
				.addIngredient(PEItems.GEM_OF_ETERNAL_DENSITY)
				.addIngredient(PEItems.RED_MATTER, 2)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.addCriterion("has_band", hasItem(PEItems.BLACK_HOLE_BAND))
				.addCriterion("has_gem", hasItem(PEItems.GEM_OF_ETERNAL_DENSITY))
				.build(consumer);
		//Watch of Flowing Time
		ShapedRecipeBuilder.shapedRecipe(PEItems.WATCH_OF_FLOWING_TIME)
				.patternLine("DGD")
				.patternLine("OCO")
				.patternLine("DGD")
				.key('C', Items.CLOCK)
				.key('D', PEItems.DARK_MATTER)
				.key('G', Items.GLOWSTONE)
				.key('O', Tags.Items.OBSIDIAN)
				.addCriterion("has_matter", hasItems(PEItems.DARK_MATTER, Items.CLOCK))
				.build(consumer);
		//Zero
		ShapedRecipeBuilder.shapedRecipe(PEItems.ZERO_RING)
				.patternLine("SBS")
				.patternLine("MIM")
				.patternLine("SBS")
				.key('B', Items.SNOWBALL)
				.key('S', Items.SNOW)
				.key('I', PEItems.IRON_BAND)
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.build(consumer);
	}

	private static void addCovalenceDustRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapelessRecipeBuilder lowCovalenceDust = ShapelessRecipeBuilder.shapelessRecipe(PEItems.LOW_COVALENCE_DUST, 40)
				.addIngredient(Items.CHARCOAL)
				.addCriterion("has_cobble", hasItem(Tags.Items.COBBLESTONE));
		for (int i = 0; i < 8; i++) {
			lowCovalenceDust.addIngredient(Tags.Items.COBBLESTONE);
		}
		lowCovalenceDust.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(PEItems.MEDIUM_COVALENCE_DUST, 40)
				.addIngredient(Tags.Items.INGOTS_IRON)
				.addIngredient(Tags.Items.DUSTS_REDSTONE)
				.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(PEItems.HIGH_COVALENCE_DUST, 40)
				.addIngredient(Tags.Items.GEMS_DIAMOND)
				.addIngredient(Items.COAL)
				.addCriterion("has_diamond", hasItem(Tags.Items.GEMS_DIAMOND))
				.build(consumer);
	}

	private static void addDiviningRodRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(PEItems.LOW_DIVINING_ROD)
				.patternLine("DDD")
				.patternLine("DSD")
				.patternLine("DDD")
				.key('S', Tags.Items.RODS_WOODEN)
				.key('D', PEItems.LOW_COVALENCE_DUST)
				.addCriterion("has_covalence_dust", hasItem(PEItems.LOW_COVALENCE_DUST))
				.build(consumer);
		diviningRodRecipe(consumer, PEItems.MEDIUM_DIVINING_ROD, PEItems.LOW_DIVINING_ROD, PEItems.MEDIUM_COVALENCE_DUST);
		diviningRodRecipe(consumer, PEItems.HIGH_DIVINING_ROD, PEItems.MEDIUM_DIVINING_ROD, PEItems.HIGH_COVALENCE_DUST);
	}

	private static void diviningRodRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider rod, IItemProvider previous, IItemProvider covalence) {
		ShapedRecipeBuilder.shapedRecipe(rod)
				.patternLine("DDD")
				.patternLine("DSD")
				.patternLine("DDD")
				.key('S', previous)
				.key('D', covalence)
				.addCriterion("has_previous", hasItem(previous))
				.build(consumer);
	}

	private static void addMiscToolRecipes(Consumer<IFinishedRecipe> consumer) {
		//Catalytic lens
		catalyticLensRecipe(consumer, false);
		catalyticLensRecipe(consumer, true);
		//Destruction Catalyst
		ShapedRecipeBuilder.shapedRecipe(PEItems.DESTRUCTION_CATALYST)
				.patternLine("NMN")
				.patternLine("MFM")
				.patternLine("NMN")
				.key('F', Items.FLINT_AND_STEEL)
				.key('M', PEItems.MOBIUS_FUEL)
				.key('N', PEBlocks.NOVA_CATALYST)
				.addCriterion("has_catalyst", hasItem(PEBlocks.NOVA_CATALYST))
				.build(consumer);
		//Evertide Amulet
		ShapedRecipeBuilder.shapedRecipe(PEItems.EVERTIDE_AMULET)
				.patternLine("WWW")
				.patternLine("DDD")
				.patternLine("WWW")
				.key('D', PEItems.DARK_MATTER)
				.key('W', Items.WATER_BUCKET)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.build(consumer);
		//Gem of Eternal Density
		ShapedRecipeBuilder.shapedRecipe(PEItems.GEM_OF_ETERNAL_DENSITY)
				.patternLine("DOD")
				.patternLine("MDM")
				.patternLine("DOD")
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('M', PEItems.DARK_MATTER)
				.key('O', Tags.Items.OBSIDIAN)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.build(consumer);
		//Hyperkinetic Lens
		ShapedRecipeBuilder.shapedRecipe(PEItems.HYPERKINETIC_LENS)
				.patternLine("DDD")
				.patternLine("MNM")
				.patternLine("DDD")
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('M', PEItems.DARK_MATTER)
				.key('N', PEBlocks.NOVA_CATALYST)
				.addCriterion("has_catalyst", hasItem(PEBlocks.NOVA_CATALYST))
				.build(consumer);
		//Mercurial Eye
		ShapedRecipeBuilder.shapedRecipe(PEItems.MERCURIAL_EYE)
				.patternLine("OBO")
				.patternLine("BRB")
				.patternLine("BDB")
				.key('B', Items.BRICKS)
				.key('R', PEItems.RED_MATTER)
				.key('D', Tags.Items.GEMS_DIAMOND)
				.key('O', Tags.Items.OBSIDIAN)
				.addCriterion("has_matter", hasItem(PEBlocks.RED_MATTER))
				.build(consumer);
		//Philosopher's Stone
		philosopherStoneRecipe(consumer, false);
		philosopherStoneRecipe(consumer, true);
		//Repair Talisman
		repairTalismanRecipe(consumer, false);
		repairTalismanRecipe(consumer, true);
		//Volcanite Amulet
		ShapedRecipeBuilder.shapedRecipe(PEItems.VOLCANITE_AMULET)
				.patternLine("LLL")
				.patternLine("DDD")
				.patternLine("LLL")
				.key('D', PEItems.DARK_MATTER)
				.key('L', Items.LAVA_BUCKET)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.build(consumer);
	}

	private static void catalyticLensRecipe(Consumer<IFinishedRecipe> consumer, boolean alternate) {
		String name = PEItems.CATALYTIC_LENS.get().getRegistryName().toString();
		ShapedRecipeBuilder lens = ShapedRecipeBuilder.shapedRecipe(PEItems.CATALYTIC_LENS)
				.patternLine("MMM")
				.patternLine(alternate ? "HMD" : "DMH")
				.patternLine("MMM")
				.key('D', PEItems.DESTRUCTION_CATALYST)
				.key('H', PEItems.HYPERKINETIC_LENS)
				.key('M', PEItems.DARK_MATTER)
				.addCriterion("has_matter", hasItem(PEItems.DARK_MATTER))
				.setGroup(name);
		if (alternate) {
			lens.build(consumer, name + "_alt");
		} else {
			lens.build(consumer);
		}
	}

	private static void philosopherStoneRecipe(Consumer<IFinishedRecipe> consumer, boolean alternate) {
		String name = PEItems.PHILOSOPHERS_STONE.get().getRegistryName().toString();
		ShapedRecipeBuilder philoStone = ShapedRecipeBuilder.shapedRecipe(PEItems.PHILOSOPHERS_STONE)
				.key('R', Tags.Items.DUSTS_REDSTONE)
				.key('G', Tags.Items.DUSTS_GLOWSTONE)
				.key('D', Tags.Items.GEMS_DIAMOND)
				.addCriterion("has_glowstone", hasItem(Tags.Items.DUSTS_GLOWSTONE))
				.setGroup(name);
		if (alternate) {
			philoStone.patternLine("GRG")
					.patternLine("RDR")
					.patternLine("GRG")
					.build(consumer, name + "_alt");
		} else {
			philoStone.patternLine("RGR")
					.patternLine("GDG")
					.patternLine("RGR")
					.build(consumer);
		}
	}

	private static void repairTalismanRecipe(Consumer<IFinishedRecipe> consumer, boolean alternate) {
		String lowToHigh = "LMH";
		String highToLow = "HML";
		String name = PEItems.REPAIR_TALISMAN.get().getRegistryName().toString();
		ShapedRecipeBuilder talisman = ShapedRecipeBuilder.shapedRecipe(PEItems.REPAIR_TALISMAN)
				.patternLine(alternate ? highToLow : lowToHigh)
				.patternLine("SPS")
				.patternLine(alternate ? lowToHigh : highToLow)
				.key('P', Items.PAPER)
				.key('S', Tags.Items.STRING)
				.key('L', PEItems.LOW_COVALENCE_DUST)
				.key('M', PEItems.MEDIUM_COVALENCE_DUST)
				.key('H', PEItems.HIGH_COVALENCE_DUST)
				.addCriterion("has_covalence_dust", hasItems(PEItems.LOW_COVALENCE_DUST, PEItems.MEDIUM_COVALENCE_DUST, PEItems.HIGH_COVALENCE_DUST))
				.setGroup(name);
		if (alternate) {
			talisman.build(consumer, name + "_alt");
		} else {
			talisman.build(consumer);
		}
	}

	private static void addTransmutationTableRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(PEBlocks.TRANSMUTATION_TABLE)
				.patternLine("OSO")
				.patternLine("SPS")
				.patternLine("OSO")
				.key('S', Tags.Items.STONE)
				.key('O', Tags.Items.OBSIDIAN)
				.key('P', PEItems.PHILOSOPHERS_STONE)
				.addCriterion("has_philo_stone", hasItem(PEItems.PHILOSOPHERS_STONE))
				.build(consumer);
		ShapedRecipeBuilder.shapedRecipe(PEItems.TRANSMUTATION_TABLET)
				.patternLine("DSD")
				.patternLine("STS")
				.patternLine("DSD")
				.key('S', Tags.Items.STONE)
				.key('D', PEBlocks.DARK_MATTER)
				.key('T', PEBlocks.TRANSMUTATION_TABLE)
				.addCriterion("has_table", hasItem(PEBlocks.TRANSMUTATION_TABLE))
				.build(consumer);
	}

	private static void addNovaRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapelessRecipe(PEBlocks.NOVA_CATALYST, 2)
				.addIngredient(Items.TNT)
				.addIngredient(PEItems.MOBIUS_FUEL)
				.addCriterion("has_tnt", hasItem(Items.TNT))
				.build(consumer);
		ShapelessRecipeBuilder.shapelessRecipe(PEBlocks.NOVA_CATACLYSM, 2)
				.addIngredient(PEBlocks.NOVA_CATALYST)
				.addIngredient(PEItems.AETERNALIS_FUEL)
				.addCriterion("has_catalyst", hasItem(PEBlocks.NOVA_CATALYST))
				.build(consumer);
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
		ShapelessRecipeBuilder.shapelessRecipe(Items.ICE)
				.addIngredient(Ingredient.fromItems(PEItems.ARCANA_RING, PEItems.ZERO_RING))
				.addIngredient(Ingredient.fromItems(Items.WATER_BUCKET, PEItems.EVERTIDE_AMULET))
				.addCriterion("has_arcana_ring", hasItem(PEItems.ARCANA_RING))
				.addCriterion("has_zero_ring", hasItem(PEItems.ZERO_RING))
				.build(consumer, PECore.rl("conversions/water_to_ice"));
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