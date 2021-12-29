package moze_intel.projecte.common.recipe;

import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.customRecipes.FullKleinStarIngredient;
import moze_intel.projecte.gameObjs.customRecipes.FullKleinStarsCondition;
import moze_intel.projecte.gameObjs.customRecipes.TomeEnabledCondition;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.gameObjs.items.KleinStar.EnumKleinTier;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.TrueCondition;

public class PERecipeProvider extends RecipeProvider {

	public PERecipeProvider(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
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
		ShapedRecipeBuilder.shaped(PEBlocks.ALCHEMICAL_CHEST)
				.pattern("LMH")
				.pattern("SDS")
				.pattern("ICI")
				.define('L', PEItems.LOW_COVALENCE_DUST)
				.define('M', PEItems.MEDIUM_COVALENCE_DUST)
				.define('H', PEItems.HIGH_COVALENCE_DUST)
				.define('S', Tags.Items.STONE)
				.define('I', Tags.Items.INGOTS_IRON)
				.define('C', Tags.Items.CHESTS_WOODEN)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_covalence_dust", hasItems(PEItems.LOW_COVALENCE_DUST, PEItems.MEDIUM_COVALENCE_DUST, PEItems.HIGH_COVALENCE_DUST))
				.save(consumer);
		//Interdiction Torch
		ShapedRecipeBuilder.shaped(PEBlocks.INTERDICTION_TORCH)
				.pattern("RDR")
				.pattern("DPD")
				.pattern("GGG")
				.define('R', Items.REDSTONE_TORCH)
				.define('G', Tags.Items.DUSTS_GLOWSTONE)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('P', PEItems.PHILOSOPHERS_STONE)
				.unlockedBy("has_philo_stone", has(PEItems.PHILOSOPHERS_STONE))
				.save(consumer);
		//Tome of Knowledge
		tomeRecipe(consumer, false);
		tomeRecipe(consumer, true);
	}

	private static void addCustomRecipeSerializer(Consumer<FinishedRecipe> consumer, SimpleRecipeSerializer<?> serializer) {
		SpecialRecipeBuilder.special(serializer).save(consumer, serializer.getRegistryName().toString());
	}

	private static void tomeRecipe(Consumer<FinishedRecipe> consumer, boolean alternate) {
		new ConditionalRecipe.Builder()
				//Tome is enabled and should use full stars
				.addCondition(TomeEnabledCondition.INSTANCE)
				.addCondition(FullKleinStarsCondition.INSTANCE)
				.addRecipe(c -> baseTomeRecipe(alternate)
						.define('K', new FullKleinStarIngredient(EnumKleinTier.OMEGA))
						.save(c))
				//Tome enabled but should not use full stars
				.addCondition(TomeEnabledCondition.INSTANCE)
				.addRecipe(c -> baseTomeRecipe(alternate)
						.define('K', PEItems.KLEIN_STAR_OMEGA)
						.save(c))
				//Add the advancement json
				.generateAdvancement()
				//Build the recipe
				.build(consumer, PECore.MODID, alternate ? "tome_alt" : "tome");
	}

	private static ShapedRecipeBuilder baseTomeRecipe(boolean alternate) {
		String lowToHigh = "LMH";
		String highToLow = "HML";
		return ShapedRecipeBuilder.shaped(PEItems.TOME_OF_KNOWLEDGE)
				.pattern(alternate ? lowToHigh : highToLow)
				.pattern("KBK")
				.pattern(alternate ? highToLow : lowToHigh)
				.define('B', Items.BOOK)
				.define('L', PEItems.LOW_COVALENCE_DUST)
				.define('M', PEItems.MEDIUM_COVALENCE_DUST)
				.define('H', PEItems.HIGH_COVALENCE_DUST)
				.unlockedBy("has_covalence_dust", hasItems(PEItems.LOW_COVALENCE_DUST, PEItems.MEDIUM_COVALENCE_DUST, PEItems.HIGH_COVALENCE_DUST))
				.group(PEItems.TOME_OF_KNOWLEDGE.get().getRegistryName().toString());
	}

	private static void addMatterRecipes(Consumer<FinishedRecipe> consumer) {
		matterBlockRecipes(consumer, PEItems.DARK_MATTER, PEBlocks.DARK_MATTER);
		matterBlockRecipes(consumer, PEItems.RED_MATTER, PEBlocks.RED_MATTER);
		darkMatterGearRecipes(consumer);
		redMatterGearRecipes(consumer);
		gemArmorRecipes(consumer);
		addFurnaceRecipes(consumer);
		//Dark Matter
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER)
				.pattern("AAA")
				.pattern("ADA")
				.pattern("AAA")
				.define('A', PEItems.AETERNALIS_FUEL)
				.define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.unlockedBy("has_aeternalis", has(PEItems.AETERNALIS_FUEL))
				.save(consumer);
		//Dark Matter Pedestal
		ShapedRecipeBuilder.shaped(PEBlocks.DARK_MATTER_PEDESTAL)
				.pattern("RDR")
				.pattern("RDR")
				.pattern("DDD")
				.define('R', PEItems.RED_MATTER)
				.define('D', PEBlocks.DARK_MATTER)
				.unlockedBy("has_matter", has(PEItems.RED_MATTER))
				.save(consumer);
		//Red Matter
		redMatterRecipe(consumer, false);
		redMatterRecipe(consumer, true);
	}

	private static void redMatterRecipe(Consumer<FinishedRecipe> consumer, boolean alternate) {
		String name = PEItems.RED_MATTER.get().getRegistryName().toString();
		ShapedRecipeBuilder redMatter = ShapedRecipeBuilder.shaped(PEItems.RED_MATTER)
				.define('A', PEItems.AETERNALIS_FUEL)
				.define('D', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.group(name);
		if (alternate) {
			redMatter.pattern("ADA")
					.pattern("ADA")
					.pattern("ADA")
					.save(consumer, name + "_alt");
		} else {
			redMatter.pattern("AAA")
					.pattern("DDD")
					.pattern("AAA")
					.save(consumer);
		}
	}

	private static void darkMatterGearRecipes(Consumer<FinishedRecipe> consumer) {
		CriterionTriggerInstance hasMatter = has(PEItems.DARK_MATTER);
		//Helmet
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_HELMET)
				.pattern("MMM")
				.pattern("M M")
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
		//Chestplate
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_CHESTPLATE)
				.pattern("M M")
				.pattern("MMM")
				.pattern("MMM")
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
		//Leggings
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_LEGGINGS)
				.pattern("MMM")
				.pattern("M M")
				.pattern("M M")
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
		//Boots
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_BOOTS)
				.pattern("M M")
				.pattern("M M")
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
		//Axe
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_AXE)
				.pattern("MM")
				.pattern("MD")
				.pattern(" D")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
		//Pickaxe
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_PICKAXE)
				.pattern("MMM")
				.pattern(" D ")
				.pattern(" D ")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
		//Shovel
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_SHOVEL)
				.pattern("M")
				.pattern("D")
				.pattern("D")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
		//Sword
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_SWORD)
				.pattern("M")
				.pattern("M")
				.pattern("D")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
		//Hoe
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_HOE)
				.pattern("MM")
				.pattern(" D")
				.pattern(" D")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
		//Shears
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_SHEARS)
				.pattern(" M")
				.pattern("D ")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
		//Hammer
		ShapedRecipeBuilder.shaped(PEItems.DARK_MATTER_HAMMER)
				.pattern("MDM")
				.pattern(" D ")
				.pattern(" D ")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(consumer);
	}

	private static void redMatterGearRecipes(Consumer<FinishedRecipe> consumer) {
		//Helmet
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_HELMET)
				.pattern("MMM")
				.pattern("MDM")
				.define('M', PEItems.RED_MATTER)
				.define('D', PEItems.DARK_MATTER_HELMET)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_HELMET))
				.save(consumer);
		//Chestplate
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_CHESTPLATE)
				.pattern("MDM")
				.pattern("MMM")
				.pattern("MMM")
				.define('M', PEItems.RED_MATTER)
				.define('D', PEItems.DARK_MATTER_CHESTPLATE)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_CHESTPLATE))
				.save(consumer);
		//Leggings
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_LEGGINGS)
				.pattern("MMM")
				.pattern("MDM")
				.pattern("M M")
				.define('M', PEItems.RED_MATTER)
				.define('D', PEItems.DARK_MATTER_LEGGINGS)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_LEGGINGS))
				.save(consumer);
		//Boots
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_BOOTS)
				.pattern("MDM")
				.pattern("M M")
				.define('M', PEItems.RED_MATTER)
				.define('D', PEItems.DARK_MATTER_BOOTS)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_BOOTS))
				.save(consumer);
		//Axe
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_AXE)
				.pattern("RR")
				.pattern("RA")
				.pattern(" M")
				.define('R', PEItems.RED_MATTER)
				.define('M', PEItems.DARK_MATTER)
				.define('A', PEItems.DARK_MATTER_AXE)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_AXE))
				.save(consumer);
		//Pickaxe
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_PICKAXE)
				.pattern("RRR")
				.pattern(" P ")
				.pattern(" M ")
				.define('R', PEItems.RED_MATTER)
				.define('M', PEItems.DARK_MATTER)
				.define('P', PEItems.DARK_MATTER_PICKAXE)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_PICKAXE))
				.save(consumer);
		//Shovel
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_SHOVEL)
				.pattern("R")
				.pattern("S")
				.pattern("M")
				.define('R', PEItems.RED_MATTER)
				.define('M', PEItems.DARK_MATTER)
				.define('S', PEItems.DARK_MATTER_SHOVEL)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_SHOVEL))
				.save(consumer);
		//Sword
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_SWORD)
				.pattern("R")
				.pattern("R")
				.pattern("S")
				.define('R', PEItems.RED_MATTER)
				.define('S', PEItems.DARK_MATTER_SWORD)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_SWORD))
				.save(consumer);
		//Hoe
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_HOE)
				.pattern("RR")
				.pattern(" H")
				.pattern(" M")
				.define('R', PEItems.RED_MATTER)
				.define('M', PEItems.DARK_MATTER)
				.define('H', PEItems.DARK_MATTER_HOE)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_HOE))
				.save(consumer);
		//Shears
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_SHEARS)
				.pattern(" R")
				.pattern("S ")
				.define('R', PEItems.RED_MATTER)
				.define('S', PEItems.DARK_MATTER_SHEARS)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_SHEARS))
				.save(consumer);
		//Hammer
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_HAMMER)
				.pattern("RMR")
				.pattern(" H ")
				.pattern(" M ")
				.define('R', PEItems.RED_MATTER)
				.define('M', PEItems.DARK_MATTER)
				.define('H', PEItems.DARK_MATTER_HAMMER)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_HAMMER))
				.save(consumer);
		//Katar (unlike the other recipes, any of the tools will work as a recipe unlock/showing)
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_KATAR)
				.pattern("123")
				.pattern("4RR")
				.pattern("RRR")
				.define('1', PEItems.RED_MATTER_SHEARS)
				.define('2', PEItems.RED_MATTER_AXE)
				.define('3', PEItems.RED_MATTER_SWORD)
				.define('4', PEItems.RED_MATTER_HOE)
				.define('R', PEItems.RED_MATTER)
				.unlockedBy("has_shears", has(PEItems.RED_MATTER_SHEARS))
				.unlockedBy("has_axe", has(PEItems.RED_MATTER_AXE))
				.unlockedBy("has_sword", has(PEItems.RED_MATTER_SWORD))
				.unlockedBy("has_hoe", has(PEItems.RED_MATTER_HOE))
				.save(consumer);
		//Morning Star (unlike the other recipes, any of the tools will work as a recipe unlock/showing)
		ShapedRecipeBuilder.shaped(PEItems.RED_MATTER_MORNING_STAR)
				.pattern("123")
				.pattern("RRR")
				.pattern("RRR")
				.define('1', PEItems.RED_MATTER_HAMMER)
				.define('2', PEItems.RED_MATTER_PICKAXE)
				.define('3', PEItems.RED_MATTER_SHOVEL)
				.define('R', PEItems.RED_MATTER)
				.unlockedBy("has_hammer", has(PEItems.RED_MATTER_HAMMER))
				.unlockedBy("has_pickaxe", has(PEItems.RED_MATTER_PICKAXE))
				.unlockedBy("has_shovel", has(PEItems.RED_MATTER_SHOVEL))
				.save(consumer);
	}

	private static void gemArmorRecipes(Consumer<FinishedRecipe> consumer) {
		//Helmet
		gemArmorRecipe(consumer, () -> ShapelessRecipeBuilder.shapeless(PEItems.GEM_HELMET)
				.requires(PEItems.RED_MATTER_HELMET)
				.requires(PEItems.EVERTIDE_AMULET)
				.requires(PEItems.SOUL_STONE)
				.unlockedBy("has_helmet", has(PEItems.RED_MATTER_HELMET)), PEItems.GEM_HELMET);
		//Chestplate
		gemArmorRecipe(consumer, () -> ShapelessRecipeBuilder.shapeless(PEItems.GEM_CHESTPLATE)
				.requires(PEItems.RED_MATTER_CHESTPLATE)
				.requires(PEItems.VOLCANITE_AMULET)
				.requires(PEItems.BODY_STONE)
				.unlockedBy("has_chestplate", has(PEItems.RED_MATTER_CHESTPLATE)), PEItems.GEM_CHESTPLATE);
		//Leggings
		gemArmorRecipe(consumer, () -> ShapelessRecipeBuilder.shapeless(PEItems.GEM_LEGGINGS)
				.requires(PEItems.RED_MATTER_LEGGINGS)
				.requires(PEItems.BLACK_HOLE_BAND)
				.requires(PEItems.WATCH_OF_FLOWING_TIME)
				.unlockedBy("has_leggings", has(PEItems.RED_MATTER_LEGGINGS)), PEItems.GEM_LEGGINGS);
		//Boots
		gemArmorRecipe(consumer, () -> ShapelessRecipeBuilder.shapeless(PEItems.GEM_BOOTS)
				.requires(PEItems.RED_MATTER_BOOTS)
				.requires(PEItems.SWIFTWOLF_RENDING_GALE, 2)
				.unlockedBy("has_boots", has(PEItems.RED_MATTER_BOOTS)), PEItems.GEM_BOOTS);
	}

	private static void gemArmorRecipe(Consumer<FinishedRecipe> consumer, Supplier<ShapelessRecipeBuilder> builder, ItemLike result) {
		new ConditionalRecipe.Builder()
				//Full stars should be used
				.addCondition(FullKleinStarsCondition.INSTANCE)
				.addRecipe(c -> builder.get()
						.requires(new FullKleinStarIngredient(EnumKleinTier.OMEGA))
						.save(c))
				//Full stars should not be used (Always true, this is the fallback)
				.addCondition(TrueCondition.INSTANCE)
				.addRecipe(c -> builder.get()
						.requires(PEItems.KLEIN_STAR_OMEGA)
						.save(c))
				//Add the advancement json
				.generateAdvancement()
				//Build the recipe
				.build(consumer, result.asItem().getRegistryName());
	}

	private static void fuelUpgradeRecipe(Consumer<FinishedRecipe> consumer, ItemLike input, ItemLike output) {
		String inputName = getName(input);
		String outputName = getName(output);
		ShapelessRecipeBuilder.shapeless(output)
				.requires(PEItems.PHILOSOPHERS_STONE)
				.requires(input, 4)
				.unlockedBy("has_" + inputName, hasItems(PEItems.PHILOSOPHERS_STONE, input))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(input, 4)
				.requires(PEItems.PHILOSOPHERS_STONE)
				.requires(output)
				.unlockedBy("has_" + outputName, hasItems(PEItems.PHILOSOPHERS_STONE, output))
				.save(consumer, PECore.rl("conversions/" + outputName + "_to_" + inputName));
	}

	private static void fuelBlockRecipes(Consumer<FinishedRecipe> consumer, ItemLike fuel, ItemLike block) {
		ShapedRecipeBuilder.shaped(block)
				.pattern("FFF")
				.pattern("FFF")
				.pattern("FFF")
				.define('F', fuel)
				.unlockedBy("has_" + getName(fuel), has(fuel))
				.save(consumer);
		String blockName = getName(block);
		ShapelessRecipeBuilder.shapeless(fuel, 9)
				.requires(block)
				.unlockedBy("has_" + blockName, has(block))
				.save(consumer, PECore.rl("conversions/" + blockName + "_deconstruct"));
	}

	private static void matterBlockRecipes(Consumer<FinishedRecipe> consumer, ItemLike matter, ItemLike block) {
		ShapedRecipeBuilder.shaped(block)
				.pattern("MM")
				.pattern("MM")
				.define('M', matter)
				.unlockedBy("has_" + getName(matter), has(matter))
				.save(consumer);
		String blockName = getName(block);
		ShapelessRecipeBuilder.shapeless(matter, 4)
				.requires(block)
				.unlockedBy("has_" + blockName, has(block))
				.save(consumer, PECore.rl("conversions/" + blockName + "_deconstruct"));
	}

	private static void addCollectorRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(PEBlocks.COLLECTOR)
				.pattern("GTG")
				.pattern("GDG")
				.pattern("GFG")
				.define('G', Items.GLOWSTONE)
				.define('F', Items.FURNACE)
				.define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.define('T', Items.GLASS)
				.unlockedBy("has_diamond", has(Tags.Items.STORAGE_BLOCKS_DIAMOND))
				.save(consumer);
		addCollectorUpgradeRecipes(consumer, PEBlocks.COLLECTOR_MK2, PEBlocks.COLLECTOR, PEItems.DARK_MATTER);
		addCollectorUpgradeRecipes(consumer, PEBlocks.COLLECTOR_MK3, PEBlocks.COLLECTOR_MK2, PEItems.RED_MATTER);
	}

	private static void addCollectorUpgradeRecipes(Consumer<FinishedRecipe> consumer, ItemLike collector, ItemLike previous, ItemLike upgradeItem) {
		ShapedRecipeBuilder.shaped(collector)
				.pattern("GUG")
				.pattern("GPG")
				.pattern("GGG")
				.define('G', Items.GLOWSTONE)
				.define('P', previous)
				.define('U', upgradeItem)
				.unlockedBy("has_previous", has(previous))
				.save(consumer);
	}

	private static void addRelayRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(PEBlocks.RELAY)
				.pattern("OSO")
				.pattern("ODO")
				.pattern("OOO")
				.define('S', Items.GLASS)
				.define('O', Tags.Items.OBSIDIAN)
				.define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.unlockedBy("has_diamond", has(Tags.Items.STORAGE_BLOCKS_DIAMOND))
				.save(consumer);
		addRelayUpgradeRecipes(consumer, PEBlocks.RELAY_MK2, PEBlocks.RELAY, PEItems.DARK_MATTER);
		addRelayUpgradeRecipes(consumer, PEBlocks.RELAY_MK3, PEBlocks.RELAY_MK2, PEItems.RED_MATTER);
	}

	private static void addRelayUpgradeRecipes(Consumer<FinishedRecipe> consumer, ItemLike relay, ItemLike previous, ItemLike upgradeItem) {
		ShapedRecipeBuilder.shaped(relay)
				.pattern("OUO")
				.pattern("OPO")
				.pattern("OOO")
				.define('O', Tags.Items.OBSIDIAN)
				.define('P', previous)
				.define('U', upgradeItem)
				.unlockedBy("has_previous", has(previous))
				.save(consumer);
	}

	private static void addCondenserRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(PEBlocks.CONDENSER)
				.pattern("ODO")
				.pattern("DCD")
				.pattern("ODO")
				.define('C', PEBlocks.ALCHEMICAL_CHEST)
				.define('O', Tags.Items.OBSIDIAN)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_alchemical_chest", has(PEBlocks.ALCHEMICAL_CHEST))
				.save(consumer);
		ShapedRecipeBuilder.shaped(PEBlocks.CONDENSER_MK2)
				.pattern("RDR")
				.pattern("DCD")
				.pattern("RDR")
				.define('R', PEBlocks.RED_MATTER)
				.define('C', PEBlocks.CONDENSER)
				.define('D', PEBlocks.DARK_MATTER)
				.unlockedBy("has_previous", has(PEBlocks.CONDENSER))
				.save(consumer);
	}

	private static void addFurnaceRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(PEBlocks.DARK_MATTER_FURNACE)
				.pattern("DDD")
				.pattern("DFD")
				.pattern("DDD")
				.define('D', PEBlocks.DARK_MATTER)
				.define('F', Items.FURNACE)
				.unlockedBy("has_dark_matter", has(PEBlocks.DARK_MATTER))
				.save(consumer);
		ShapedRecipeBuilder.shaped(PEBlocks.RED_MATTER_FURNACE)
				.pattern(" R ")
				.pattern("RFR")
				.define('R', PEBlocks.RED_MATTER)
				.define('F', PEBlocks.DARK_MATTER_FURNACE)
				.unlockedBy("has_previous", has(PEBlocks.DARK_MATTER_FURNACE))
				.save(consumer);
	}

	private static void addKleinRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(PEItems.KLEIN_STAR_EIN)
				.pattern("MMM")
				.pattern("MDM")
				.pattern("MMM")
				.define('M', PEItems.MOBIUS_FUEL)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_components", hasItems(PEItems.MOBIUS_FUEL, Tags.Items.GEMS_DIAMOND))
				.save(consumer);
		EnumKleinTier[] tiers = EnumKleinTier.values();
		for (int tier = 1; tier < tiers.length; tier++) {
			kleinStarUpgrade(consumer, PEItems.getStar(tiers[tier]), PEItems.getStar(tiers[tier - 1]));
		}
	}

	private static void kleinStarUpgrade(Consumer<FinishedRecipe> consumer, ItemLike star, ItemLike previous) {
		//Wrap the consumer so that we can replace it with the proper serializer
		ShapelessRecipeBuilder.shapeless(star)
				.requires(previous, 4)
				.unlockedBy("has_components", has(previous))
				.save(recipe -> consumer.accept(new ShapelessKleinStarRecipeResult(recipe)));
	}

	private static void addRingRecipes(Consumer<FinishedRecipe> consumer) {
		//Arcana (Any ring or red matter)
		ShapedRecipeBuilder.shaped(PEItems.ARCANA_RING)
				.pattern("ZIH")
				.pattern("SMM")
				.pattern("MMM")
				.define('S', PEItems.SWIFTWOLF_RENDING_GALE)
				.define('H', PEItems.HARVEST_GODDESS_BAND)
				.define('I', PEItems.IGNITION_RING)
				.define('Z', PEItems.ZERO_RING)
				.define('M', PEItems.RED_MATTER)
				.unlockedBy("has_swrg", has(PEItems.SWIFTWOLF_RENDING_GALE))
				.unlockedBy("has_harvest", has(PEItems.HARVEST_GODDESS_BAND))
				.unlockedBy("has_ignition", has(PEItems.IGNITION_RING))
				.unlockedBy("has_zero", has(PEItems.ZERO_RING))
				.unlockedBy("has_matter", has(PEItems.RED_MATTER))
				.save(consumer);
		//Archangel Smite
		ShapedRecipeBuilder.shaped(PEItems.ARCHANGEL_SMITE)
				.pattern("BFB")
				.pattern("MIM")
				.pattern("BFB")
				.define('B', Items.BOW)
				.define('F', Tags.Items.FEATHERS)
				.define('I', PEItems.IRON_BAND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(consumer);
		//Black Hole Band
		ShapedRecipeBuilder.shaped(PEItems.BLACK_HOLE_BAND)
				.pattern("SSS")
				.pattern("DID")
				.pattern("SSS")
				.define('S', Tags.Items.STRING)
				.define('D', PEItems.DARK_MATTER)
				.define('I', PEItems.IRON_BAND)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(consumer);
		//Body Stone
		ShapedRecipeBuilder.shaped(PEItems.BODY_STONE)
				.pattern("SSS")
				.pattern("RLR")
				.pattern("SSS")
				.define('R', PEItems.RED_MATTER)
				.define('S', Items.SUGAR)
				.define('L', Tags.Items.GEMS_LAPIS)
				.unlockedBy("has_matter", has(PEItems.RED_MATTER))
				.save(consumer);
		//Harvest Goddess
		ShapedRecipeBuilder.shaped(PEItems.HARVEST_GODDESS_BAND)
				.pattern("SFS")
				.pattern("DID")
				.pattern("SFS")
				.define('S', ItemTags.SAPLINGS)
				.define('D', PEItems.DARK_MATTER)
				.define('F', Items.POPPY)
				.define('I', PEItems.IRON_BAND)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(consumer);
		//Ignition
		ShapedRecipeBuilder.shaped(PEItems.IGNITION_RING)
				.pattern("FMF")
				.pattern("DID")
				.pattern("FMF")
				.define('D', PEItems.DARK_MATTER)
				.define('F', Items.FLINT_AND_STEEL)
				.define('I', PEItems.IRON_BAND)
				.define('M', PEItems.MOBIUS_FUEL)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(consumer);
		//Iron Band
		ShapedRecipeBuilder.shaped(PEItems.IRON_BAND)
				.pattern("III")
				.pattern("ILI")
				.pattern("III")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('L', Ingredient.of(Items.LAVA_BUCKET, PEItems.VOLCANITE_AMULET))
				.unlockedBy("has_lava", has(Items.LAVA_BUCKET))
				.unlockedBy("has_amulet", has(PEItems.VOLCANITE_AMULET))
				.save(consumer);
		//Life Stone
		ShapelessRecipeBuilder.shapeless(PEItems.LIFE_STONE)
				.requires(PEItems.BODY_STONE)
				.requires(PEItems.SOUL_STONE)
				.unlockedBy("has_body", has(PEItems.BODY_STONE))
				.unlockedBy("has_soul", has(PEItems.SOUL_STONE))
				.save(consumer);
		//Mind Stone
		ShapedRecipeBuilder.shaped(PEItems.MIND_STONE)
				.pattern("BBB")
				.pattern("RLR")
				.pattern("BBB")
				.define('R', PEItems.RED_MATTER)
				.define('B', Items.BOOK)
				.define('L', Tags.Items.GEMS_LAPIS)
				.unlockedBy("has_matter", has(PEItems.RED_MATTER))
				.save(consumer);
		//Soul Stone
		ShapedRecipeBuilder.shaped(PEItems.SOUL_STONE)
				.pattern("GGG")
				.pattern("RLR")
				.pattern("GGG")
				.define('R', PEItems.RED_MATTER)
				.define('G', Tags.Items.DUSTS_GLOWSTONE)
				.define('L', Tags.Items.GEMS_LAPIS)
				.unlockedBy("has_matter", has(PEItems.RED_MATTER))
				.save(consumer);
		//SWRG
		ShapedRecipeBuilder.shaped(PEItems.SWIFTWOLF_RENDING_GALE)
				.pattern("DFD")
				.pattern("FIF")
				.pattern("DFD")
				.define('D', PEItems.DARK_MATTER)
				.define('F', Tags.Items.FEATHERS)
				.define('I', PEItems.IRON_BAND)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(consumer);
		//Void Ring
		ShapelessRecipeBuilder.shapeless(PEItems.VOID_RING)
				.requires(PEItems.BLACK_HOLE_BAND)
				.requires(PEItems.GEM_OF_ETERNAL_DENSITY)
				.requires(PEItems.RED_MATTER, 2)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.unlockedBy("has_band", has(PEItems.BLACK_HOLE_BAND))
				.unlockedBy("has_gem", has(PEItems.GEM_OF_ETERNAL_DENSITY))
				.save(consumer);
		//Watch of Flowing Time
		ShapedRecipeBuilder.shaped(PEItems.WATCH_OF_FLOWING_TIME)
				.pattern("DGD")
				.pattern("OCO")
				.pattern("DGD")
				.define('C', Items.CLOCK)
				.define('D', PEItems.DARK_MATTER)
				.define('G', Items.GLOWSTONE)
				.define('O', Tags.Items.OBSIDIAN)
				.unlockedBy("has_matter", hasItems(PEItems.DARK_MATTER, Items.CLOCK))
				.save(consumer);
		//Zero
		ShapedRecipeBuilder.shaped(PEItems.ZERO_RING)
				.pattern("SBS")
				.pattern("MIM")
				.pattern("SBS")
				.define('B', Items.SNOWBALL)
				.define('S', Items.SNOW)
				.define('I', PEItems.IRON_BAND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(consumer);
	}

	private static void addCovalenceDustRecipes(Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder lowCovalenceDust = ShapelessRecipeBuilder.shapeless(PEItems.LOW_COVALENCE_DUST, 40)
				.requires(Items.CHARCOAL)
				.unlockedBy("has_cobble", has(Tags.Items.COBBLESTONE));
		for (int i = 0; i < 8; i++) {
			lowCovalenceDust.requires(Tags.Items.COBBLESTONE);
		}
		lowCovalenceDust.save(consumer);
		ShapelessRecipeBuilder.shapeless(PEItems.MEDIUM_COVALENCE_DUST, 40)
				.requires(Tags.Items.INGOTS_IRON)
				.requires(Tags.Items.DUSTS_REDSTONE)
				.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(PEItems.HIGH_COVALENCE_DUST, 40)
				.requires(Tags.Items.GEMS_DIAMOND)
				.requires(Items.COAL)
				.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
				.save(consumer);
	}

	private static void addDiviningRodRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(PEItems.LOW_DIVINING_ROD)
				.pattern("DDD")
				.pattern("DSD")
				.pattern("DDD")
				.define('S', Tags.Items.RODS_WOODEN)
				.define('D', PEItems.LOW_COVALENCE_DUST)
				.unlockedBy("has_covalence_dust", has(PEItems.LOW_COVALENCE_DUST))
				.save(consumer);
		diviningRodRecipe(consumer, PEItems.MEDIUM_DIVINING_ROD, PEItems.LOW_DIVINING_ROD, PEItems.MEDIUM_COVALENCE_DUST);
		diviningRodRecipe(consumer, PEItems.HIGH_DIVINING_ROD, PEItems.MEDIUM_DIVINING_ROD, PEItems.HIGH_COVALENCE_DUST);
	}

	private static void diviningRodRecipe(Consumer<FinishedRecipe> consumer, ItemLike rod, ItemLike previous, ItemLike covalence) {
		ShapedRecipeBuilder.shaped(rod)
				.pattern("DDD")
				.pattern("DSD")
				.pattern("DDD")
				.define('S', previous)
				.define('D', covalence)
				.unlockedBy("has_previous", has(previous))
				.save(consumer);
	}

	private static void addMiscToolRecipes(Consumer<FinishedRecipe> consumer) {
		//Catalytic lens
		catalyticLensRecipe(consumer, false);
		catalyticLensRecipe(consumer, true);
		//Destruction Catalyst
		ShapedRecipeBuilder.shaped(PEItems.DESTRUCTION_CATALYST)
				.pattern("NMN")
				.pattern("MFM")
				.pattern("NMN")
				.define('F', Items.FLINT_AND_STEEL)
				.define('M', PEItems.MOBIUS_FUEL)
				.define('N', PEBlocks.NOVA_CATALYST)
				.unlockedBy("has_catalyst", has(PEBlocks.NOVA_CATALYST))
				.save(consumer);
		//Evertide Amulet
		ShapedRecipeBuilder.shaped(PEItems.EVERTIDE_AMULET)
				.pattern("WWW")
				.pattern("DDD")
				.pattern("WWW")
				.define('D', PEItems.DARK_MATTER)
				.define('W', Items.WATER_BUCKET)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(consumer);
		//Gem of Eternal Density
		ShapedRecipeBuilder.shaped(PEItems.GEM_OF_ETERNAL_DENSITY)
				.pattern("DOD")
				.pattern("MDM")
				.pattern("DOD")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.define('O', Tags.Items.OBSIDIAN)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(consumer);
		//Hyperkinetic Lens
		ShapedRecipeBuilder.shaped(PEItems.HYPERKINETIC_LENS)
				.pattern("DDD")
				.pattern("MNM")
				.pattern("DDD")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.define('N', PEBlocks.NOVA_CATALYST)
				.unlockedBy("has_catalyst", has(PEBlocks.NOVA_CATALYST))
				.save(consumer);
		//Mercurial Eye
		ShapedRecipeBuilder.shaped(PEItems.MERCURIAL_EYE)
				.pattern("OBO")
				.pattern("BRB")
				.pattern("BDB")
				.define('B', Items.BRICKS)
				.define('R', PEItems.RED_MATTER)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('O', Tags.Items.OBSIDIAN)
				.unlockedBy("has_matter", has(PEBlocks.RED_MATTER))
				.save(consumer);
		//Philosopher's Stone
		philosopherStoneRecipe(consumer, false);
		philosopherStoneRecipe(consumer, true);
		//Repair Talisman
		repairTalismanRecipe(consumer, false);
		repairTalismanRecipe(consumer, true);
		//Volcanite Amulet
		ShapedRecipeBuilder.shaped(PEItems.VOLCANITE_AMULET)
				.pattern("LLL")
				.pattern("DDD")
				.pattern("LLL")
				.define('D', PEItems.DARK_MATTER)
				.define('L', Items.LAVA_BUCKET)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(consumer);
	}

	private static void catalyticLensRecipe(Consumer<FinishedRecipe> consumer, boolean alternate) {
		String name = PEItems.CATALYTIC_LENS.get().getRegistryName().toString();
		ShapedRecipeBuilder lens = ShapedRecipeBuilder.shaped(PEItems.CATALYTIC_LENS)
				.pattern("MMM")
				.pattern(alternate ? "HMD" : "DMH")
				.pattern("MMM")
				.define('D', PEItems.DESTRUCTION_CATALYST)
				.define('H', PEItems.HYPERKINETIC_LENS)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.group(name);
		if (alternate) {
			lens.save(consumer, name + "_alt");
		} else {
			lens.save(consumer);
		}
	}

	private static void philosopherStoneRecipe(Consumer<FinishedRecipe> consumer, boolean alternate) {
		String name = PEItems.PHILOSOPHERS_STONE.get().getRegistryName().toString();
		ShapedRecipeBuilder philoStone = ShapedRecipeBuilder.shaped(PEItems.PHILOSOPHERS_STONE)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('G', Tags.Items.DUSTS_GLOWSTONE)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_glowstone", has(Tags.Items.DUSTS_GLOWSTONE))
				.group(name);
		if (alternate) {
			philoStone.pattern("GRG")
					.pattern("RDR")
					.pattern("GRG")
					.save(consumer, name + "_alt");
		} else {
			philoStone.pattern("RGR")
					.pattern("GDG")
					.pattern("RGR")
					.save(consumer);
		}
	}

	private static void repairTalismanRecipe(Consumer<FinishedRecipe> consumer, boolean alternate) {
		String lowToHigh = "LMH";
		String highToLow = "HML";
		String name = PEItems.REPAIR_TALISMAN.get().getRegistryName().toString();
		ShapedRecipeBuilder talisman = ShapedRecipeBuilder.shaped(PEItems.REPAIR_TALISMAN)
				.pattern(alternate ? highToLow : lowToHigh)
				.pattern("SPS")
				.pattern(alternate ? lowToHigh : highToLow)
				.define('P', Items.PAPER)
				.define('S', Tags.Items.STRING)
				.define('L', PEItems.LOW_COVALENCE_DUST)
				.define('M', PEItems.MEDIUM_COVALENCE_DUST)
				.define('H', PEItems.HIGH_COVALENCE_DUST)
				.unlockedBy("has_covalence_dust", hasItems(PEItems.LOW_COVALENCE_DUST, PEItems.MEDIUM_COVALENCE_DUST, PEItems.HIGH_COVALENCE_DUST))
				.group(name);
		if (alternate) {
			talisman.save(consumer, name + "_alt");
		} else {
			talisman.save(consumer);
		}
	}

	private static void addTransmutationTableRecipes(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(PEBlocks.TRANSMUTATION_TABLE)
				.pattern("OSO")
				.pattern("SPS")
				.pattern("OSO")
				.define('S', Tags.Items.STONE)
				.define('O', Tags.Items.OBSIDIAN)
				.define('P', PEItems.PHILOSOPHERS_STONE)
				.unlockedBy("has_philo_stone", has(PEItems.PHILOSOPHERS_STONE))
				.save(consumer);
		ShapedRecipeBuilder.shaped(PEItems.TRANSMUTATION_TABLET)
				.pattern("DSD")
				.pattern("STS")
				.pattern("DSD")
				.define('S', Tags.Items.STONE)
				.define('D', PEBlocks.DARK_MATTER)
				.define('T', PEBlocks.TRANSMUTATION_TABLE)
				.unlockedBy("has_table", has(PEBlocks.TRANSMUTATION_TABLE))
				.save(consumer);
	}

	private static void addNovaRecipes(Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(PEBlocks.NOVA_CATALYST, 2)
				.requires(Items.TNT)
				.requires(PEItems.MOBIUS_FUEL)
				.unlockedBy("has_tnt", has(Items.TNT))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(PEBlocks.NOVA_CATACLYSM, 2)
				.requires(PEBlocks.NOVA_CATALYST)
				.requires(PEItems.AETERNALIS_FUEL)
				.unlockedBy("has_catalyst", has(PEBlocks.NOVA_CATALYST))
				.save(consumer);
	}

	private static void addBagRecipes(Consumer<FinishedRecipe> consumer) {
		CriterionTriggerInstance hasChest = has(PEBlocks.ALCHEMICAL_CHEST);
		CriterionTriggerInstance hasBag = has(PETags.Items.ALCHEMICAL_BAGS);
		for (DyeColor color : DyeColor.values()) {
			AlchemicalBag bag = PEItems.getBag(color);
			//Crafting recipe
			ShapedRecipeBuilder.shaped(bag)
					.pattern("CCC")
					.pattern("WAW")
					.pattern("WWW")
					.define('A', PEBlocks.ALCHEMICAL_CHEST)
					.define('C', PEItems.HIGH_COVALENCE_DUST)
					.define('W', getWool(color))
					.unlockedBy("has_alchemical_chest", hasChest)
					.save(consumer);
			//Dye bag conversion recipes
			ShapelessRecipeBuilder.shapeless(bag)
					.requires(PETags.Items.ALCHEMICAL_BAGS)
					.requires(color.getTag())
					.unlockedBy("has_alchemical_bag", hasBag)
					.save(consumer, PECore.rl("conversions/dye_bag_" + color));
		}
	}

	private static ItemLike getWool(DyeColor color) {
		return switch (color) {
			case WHITE -> Items.WHITE_WOOL;
			case ORANGE -> Items.ORANGE_WOOL;
			case MAGENTA -> Items.MAGENTA_WOOL;
			case LIGHT_BLUE -> Items.LIGHT_BLUE_WOOL;
			case YELLOW -> Items.YELLOW_WOOL;
			case LIME -> Items.LIME_WOOL;
			case PINK -> Items.PINK_WOOL;
			case GRAY -> Items.GRAY_WOOL;
			case LIGHT_GRAY -> Items.LIGHT_GRAY_WOOL;
			case CYAN -> Items.CYAN_WOOL;
			case PURPLE -> Items.PURPLE_WOOL;
			case BLUE -> Items.BLUE_WOOL;
			case BROWN -> Items.BROWN_WOOL;
			case GREEN -> Items.GREEN_WOOL;
			case RED -> Items.RED_WOOL;
			case BLACK -> Items.BLACK_WOOL;
		};
	}

	private static void addConversionRecipes(Consumer<FinishedRecipe> consumer) {
		philoConversionRecipe(consumer, Items.CHARCOAL, 4, Items.COAL, 1);
		philoConversionRecipe(consumer, Tags.Items.GEMS_DIAMOND, Items.DIAMOND, 2, Tags.Items.GEMS_EMERALD, Items.EMERALD, 1);
		philoConversionRecipe(consumer, Tags.Items.INGOTS_GOLD, Items.GOLD_INGOT, 4, Tags.Items.GEMS_DIAMOND, Items.DIAMOND, 1);
		philoConversionRecipe(consumer, Tags.Items.INGOTS_IRON, Items.IRON_INGOT, 8, Tags.Items.INGOTS_GOLD, Items.GOLD_INGOT, 1);
		//Iron -> Ender Pearl
		philoConversionRecipe(consumer, getName(Items.IRON_INGOT), Tags.Items.INGOTS_IRON, 4, getName(Items.ENDER_PEARL), Items.ENDER_PEARL, 1);
		//Dirt -> Grass
		ShapelessRecipeBuilder.shapeless(Items.GRASS_BLOCK)
				.requires(PEItems.ARCANA_RING)
				.requires(Items.DIRT)
				.unlockedBy("has_arcana_ring", has(PEItems.ARCANA_RING))
				.save(consumer, PECore.rl("conversions/dirt_to_grass"));
		//Redstone -> Lava
		ShapelessRecipeBuilder.shapeless(Items.LAVA_BUCKET)
				.requires(PEItems.VOLCANITE_AMULET)
				.requires(Items.BUCKET)
				.requires(Tags.Items.DUSTS_REDSTONE)
				.unlockedBy("has_volcanite_amulet", has(PEItems.VOLCANITE_AMULET))
				.save(consumer, PECore.rl("conversions/redstone_to_lava"));
		//Water -> Ice
		ShapelessRecipeBuilder.shapeless(Items.ICE)
				.requires(Ingredient.of(PEItems.ARCANA_RING, PEItems.ZERO_RING))
				.requires(Ingredient.of(Items.WATER_BUCKET, PEItems.EVERTIDE_AMULET))
				.unlockedBy("has_arcana_ring", has(PEItems.ARCANA_RING))
				.unlockedBy("has_zero_ring", has(PEItems.ZERO_RING))
				.save(consumer, PECore.rl("conversions/water_to_ice"));
	}

	private static void philoConversionRecipe(Consumer<FinishedRecipe> consumer, ItemLike a, int aAmount, ItemLike b, int bAmount) {
		String aName = getName(a);
		String bName = getName(b);
		ShapelessRecipeBuilder.shapeless(b, bAmount)
				.requires(PEItems.PHILOSOPHERS_STONE)
				.requires(a, aAmount)
				.unlockedBy("has_" + aName, hasItems(PEItems.PHILOSOPHERS_STONE, a))
				.save(consumer, PECore.rl("conversions/" + aName + "_to_" + bName));
		ShapelessRecipeBuilder.shapeless(a, aAmount)
				.requires(PEItems.PHILOSOPHERS_STONE)
				.requires(b, bAmount)
				.unlockedBy("has_" + bName, hasItems(PEItems.PHILOSOPHERS_STONE, b))
				.save(consumer, PECore.rl("conversions/" + bName + "_to_" + aName));
	}

	private static void philoConversionRecipe(Consumer<FinishedRecipe> consumer, Tag<Item> aTag, ItemLike a, int aAmount, Tag<Item> bTag, ItemLike b,
			int bAmount) {
		String aName = getName(a);
		String bName = getName(b);
		//A to B
		philoConversionRecipe(consumer, aName, aTag, aAmount, bName, b, bAmount);
		//B to A
		philoConversionRecipe(consumer, bName, bTag, bAmount, aName, a, aAmount);
	}

	private static void philoConversionRecipe(Consumer<FinishedRecipe> consumer, String inputName, Tag<Item> inputTag, int inputAmount, String outputName,
			ItemLike output, int outputAmount) {
		ShapelessRecipeBuilder bToA = ShapelessRecipeBuilder.shapeless(output, outputAmount)
				.requires(PEItems.PHILOSOPHERS_STONE)
				.unlockedBy("has_" + inputName, hasItems(PEItems.PHILOSOPHERS_STONE, inputTag));
		for (int i = 0; i < inputAmount; i++) {
			bToA.requires(inputTag);
		}
		bToA.save(consumer, PECore.rl("conversions/" + inputName + "_to_" + outputName));
	}

	private static String getName(ItemLike item) {
		return item.asItem().getRegistryName().getPath();
	}

	protected static InventoryChangeTrigger.TriggerInstance hasItems(ItemLike... items) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(items);
	}

	@SafeVarargs
	protected static InventoryChangeTrigger.TriggerInstance hasItems(ItemLike item, Tag<Item>... tags) {
		return hasItems(new ItemLike[]{item}, tags);
	}

	@SafeVarargs
	protected static InventoryChangeTrigger.TriggerInstance hasItems(ItemLike[] items, Tag<Item>... tags) {
		ItemPredicate[] predicates = new ItemPredicate[items.length + tags.length];
		for (int i = 0; i < items.length; ++i) {
			predicates[i] = ItemPredicate.Builder.item().of(items[i]).build();
		}
		for (int i = 0; i < tags.length; ++i) {
			predicates[items.length + i] = ItemPredicate.Builder.item().of(tags[i]).build();
		}
		return inventoryTrigger(predicates);
	}
}