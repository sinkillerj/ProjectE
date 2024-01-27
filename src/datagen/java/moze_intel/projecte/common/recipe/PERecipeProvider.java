package moze_intel.projecte.common.recipe;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import moze_intel.projecte.gameObjs.customRecipes.FullKleinStarsCondition;
import moze_intel.projecte.gameObjs.customRecipes.PhiloStoneSmeltingRecipe;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessKleinStar;
import moze_intel.projecte.gameObjs.customRecipes.RecipesCovalenceRepair;
import moze_intel.projecte.gameObjs.customRecipes.TomeEnabledCondition;
import moze_intel.projecte.gameObjs.items.AlchemicalBag;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.items.KleinStar.EnumKleinTier;
import moze_intel.projecte.gameObjs.registration.impl.ItemRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.Constants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.crafting.NBTIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PERecipeProvider extends RecipeProvider {

	public PERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider);
	}

	@Override
	protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
		SpecialRecipeBuilder.special(RecipesCovalenceRepair::new)
				.save(recipeOutput, PECore.rl("covalence_repair"));
		SpecialRecipeBuilder.special(PhiloStoneSmeltingRecipe::new)
				.save(recipeOutput, PECore.rl("philo_stone_smelting"));
		fuelUpgradeRecipe(recipeOutput, Items.COAL, PEItems.ALCHEMICAL_COAL);
		fuelUpgradeRecipe(recipeOutput, PEItems.ALCHEMICAL_COAL, PEItems.MOBIUS_FUEL);
		fuelUpgradeRecipe(recipeOutput, PEItems.MOBIUS_FUEL, PEItems.AETERNALIS_FUEL);
		fuelBlockRecipes(recipeOutput, PEItems.ALCHEMICAL_COAL, PEBlocks.ALCHEMICAL_COAL);
		fuelBlockRecipes(recipeOutput, PEItems.MOBIUS_FUEL, PEBlocks.MOBIUS_FUEL);
		fuelBlockRecipes(recipeOutput, PEItems.AETERNALIS_FUEL, PEBlocks.AETERNALIS_FUEL);
		addMatterRecipes(recipeOutput);
		addBagRecipes(recipeOutput);
		addCollectorRecipes(recipeOutput);
		addRelayRecipes(recipeOutput);
		addCondenserRecipes(recipeOutput);
		addTransmutationTableRecipes(recipeOutput);
		addNovaRecipes(recipeOutput);
		addKleinRecipes(recipeOutput);
		addRingRecipes(recipeOutput);
		addCovalenceDustRecipes(recipeOutput);
		addDiviningRodRecipes(recipeOutput);
		addMiscToolRecipes(recipeOutput);
		//Conversion recipes
		addConversionRecipes(recipeOutput);
		//Alchemical Chest
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEBlocks.ALCHEMICAL_CHEST)
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
				.save(recipeOutput);
		//Interdiction Torch
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEBlocks.INTERDICTION_TORCH)
				.pattern("RDR")
				.pattern("DPD")
				.pattern("GGG")
				.define('R', Items.REDSTONE_TORCH)
				.define('G', Tags.Items.DUSTS_GLOWSTONE)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('P', PEItems.PHILOSOPHERS_STONE)
				.unlockedBy("has_philo_stone", has(PEItems.PHILOSOPHERS_STONE))
				.save(recipeOutput);
		//Tome of Knowledge
		tomeRecipe(recipeOutput, false);
		tomeRecipe(recipeOutput, true);
	}

	private static void tomeRecipe(RecipeOutput recipeOutput, boolean alternate) {
		ResourceLocation name = PECore.rl(alternate ? "tome_alt" : "tome");
		//Tome is enabled and should use full stars
		baseTomeRecipe(alternate)
				.define('K', getFullKleinStarIngredient(EnumKleinTier.OMEGA))
				.save(recipeOutput.withConditions(TomeEnabledCondition.INSTANCE, FullKleinStarsCondition.INSTANCE), name.withPrefix("full_star_"));
		//Tome enabled but should not use full stars
		baseTomeRecipe(alternate)
				.define('K', PEItems.KLEIN_STAR_OMEGA)
				.save(recipeOutput.withConditions(TomeEnabledCondition.INSTANCE, new NotCondition(FullKleinStarsCondition.INSTANCE)), name);
	}

	private static ShapedRecipeBuilder baseTomeRecipe(boolean alternate) {
		String lowToHigh = "LMH";
		String highToLow = "HML";
		return ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.TOME_OF_KNOWLEDGE)
				.pattern(alternate ? lowToHigh : highToLow)
				.pattern("KBK")
				.pattern(alternate ? highToLow : lowToHigh)
				.define('B', Items.BOOK)
				.define('L', PEItems.LOW_COVALENCE_DUST)
				.define('M', PEItems.MEDIUM_COVALENCE_DUST)
				.define('H', PEItems.HIGH_COVALENCE_DUST)
				.unlockedBy("has_covalence_dust", hasItems(PEItems.LOW_COVALENCE_DUST, PEItems.MEDIUM_COVALENCE_DUST, PEItems.HIGH_COVALENCE_DUST))
				.group(PEItems.TOME_OF_KNOWLEDGE.getId().toString());
	}

	private static void addMatterRecipes(RecipeOutput recipeOutput) {
		matterBlockRecipes(recipeOutput, PEItems.DARK_MATTER, PEBlocks.DARK_MATTER);
		matterBlockRecipes(recipeOutput, PEItems.RED_MATTER, PEBlocks.RED_MATTER);
		darkMatterGearRecipes(recipeOutput);
		redMatterGearRecipes(recipeOutput);
		gemArmorRecipes(recipeOutput);
		addFurnaceRecipes(recipeOutput);
		//Dark Matter
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PEItems.DARK_MATTER)
				.pattern("AAA")
				.pattern("ADA")
				.pattern("AAA")
				.define('A', PEItems.AETERNALIS_FUEL)
				.define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.unlockedBy("has_aeternalis", has(PEItems.AETERNALIS_FUEL))
				.save(recipeOutput);
		//Dark Matter Pedestal
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEBlocks.DARK_MATTER_PEDESTAL)
				.pattern("RDR")
				.pattern("RDR")
				.pattern("DDD")
				.define('R', PEItems.RED_MATTER)
				.define('D', PEBlocks.DARK_MATTER)
				.unlockedBy("has_matter", has(PEItems.RED_MATTER))
				.save(recipeOutput);
		//Red Matter
		redMatterRecipe(recipeOutput, false);
		redMatterRecipe(recipeOutput, true);
	}

	private static void redMatterRecipe(RecipeOutput recipeOutput, boolean alternate) {
		String name = PEItems.RED_MATTER.getId().toString();
		ShapedRecipeBuilder redMatter = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, PEItems.RED_MATTER)
				.define('A', PEItems.AETERNALIS_FUEL)
				.define('D', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.group(name);
		if (alternate) {
			redMatter.pattern("ADA")
					.pattern("ADA")
					.pattern("ADA")
					.save(recipeOutput, name + "_alt");
		} else {
			redMatter.pattern("AAA")
					.pattern("DDD")
					.pattern("AAA")
					.save(recipeOutput);
		}
	}

	private static void darkMatterGearRecipes(RecipeOutput recipeOutput) {
		Criterion<InventoryChangeTrigger.TriggerInstance> hasMatter = has(PEItems.DARK_MATTER);
		//Helmet
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.DARK_MATTER_HELMET)
				.pattern("MMM")
				.pattern("M M")
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
		//Chestplate
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.DARK_MATTER_CHESTPLATE)
				.pattern("M M")
				.pattern("MMM")
				.pattern("MMM")
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
		//Leggings
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.DARK_MATTER_LEGGINGS)
				.pattern("MMM")
				.pattern("M M")
				.pattern("M M")
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
		//Boots
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.DARK_MATTER_BOOTS)
				.pattern("M M")
				.pattern("M M")
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
		//Axe
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.DARK_MATTER_AXE)
				.pattern("MM")
				.pattern("MD")
				.pattern(" D")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
		//Pickaxe
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.DARK_MATTER_PICKAXE)
				.pattern("MMM")
				.pattern(" D ")
				.pattern(" D ")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
		//Shovel
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.DARK_MATTER_SHOVEL)
				.pattern("M")
				.pattern("D")
				.pattern("D")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
		//Sword
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.DARK_MATTER_SWORD)
				.pattern("M")
				.pattern("M")
				.pattern("D")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
		//Hoe
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.DARK_MATTER_HOE)
				.pattern("MM")
				.pattern(" D")
				.pattern(" D")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
		//Shears
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.DARK_MATTER_SHEARS)
				.pattern(" M")
				.pattern("D ")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
		//Hammer
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.DARK_MATTER_HAMMER)
				.pattern("MDM")
				.pattern(" D ")
				.pattern(" D ")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", hasMatter)
				.save(recipeOutput);
	}

	private static void redMatterGearRecipes(RecipeOutput recipeOutput) {
		//Helmet
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.RED_MATTER_HELMET)
				.pattern("MMM")
				.pattern("MDM")
				.define('M', PEItems.RED_MATTER)
				.define('D', PEItems.DARK_MATTER_HELMET)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_HELMET))
				.save(recipeOutput);
		//Chestplate
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.RED_MATTER_CHESTPLATE)
				.pattern("MDM")
				.pattern("MMM")
				.pattern("MMM")
				.define('M', PEItems.RED_MATTER)
				.define('D', PEItems.DARK_MATTER_CHESTPLATE)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_CHESTPLATE))
				.save(recipeOutput);
		//Leggings
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.RED_MATTER_LEGGINGS)
				.pattern("MMM")
				.pattern("MDM")
				.pattern("M M")
				.define('M', PEItems.RED_MATTER)
				.define('D', PEItems.DARK_MATTER_LEGGINGS)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_LEGGINGS))
				.save(recipeOutput);
		//Boots
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.RED_MATTER_BOOTS)
				.pattern("MDM")
				.pattern("M M")
				.define('M', PEItems.RED_MATTER)
				.define('D', PEItems.DARK_MATTER_BOOTS)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_BOOTS))
				.save(recipeOutput);
		//Axe
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.RED_MATTER_AXE)
				.pattern("RR")
				.pattern("RA")
				.pattern(" M")
				.define('R', PEItems.RED_MATTER)
				.define('M', PEItems.DARK_MATTER)
				.define('A', PEItems.DARK_MATTER_AXE)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_AXE))
				.save(recipeOutput);
		//Pickaxe
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.RED_MATTER_PICKAXE)
				.pattern("RRR")
				.pattern(" P ")
				.pattern(" M ")
				.define('R', PEItems.RED_MATTER)
				.define('M', PEItems.DARK_MATTER)
				.define('P', PEItems.DARK_MATTER_PICKAXE)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_PICKAXE))
				.save(recipeOutput);
		//Shovel
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.RED_MATTER_SHOVEL)
				.pattern("R")
				.pattern("S")
				.pattern("M")
				.define('R', PEItems.RED_MATTER)
				.define('M', PEItems.DARK_MATTER)
				.define('S', PEItems.DARK_MATTER_SHOVEL)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_SHOVEL))
				.save(recipeOutput);
		//Sword
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.RED_MATTER_SWORD)
				.pattern("R")
				.pattern("R")
				.pattern("S")
				.define('R', PEItems.RED_MATTER)
				.define('S', PEItems.DARK_MATTER_SWORD)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_SWORD))
				.save(recipeOutput);
		//Hoe
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.RED_MATTER_HOE)
				.pattern("RR")
				.pattern(" H")
				.pattern(" M")
				.define('R', PEItems.RED_MATTER)
				.define('M', PEItems.DARK_MATTER)
				.define('H', PEItems.DARK_MATTER_HOE)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_HOE))
				.save(recipeOutput);
		//Shears
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.RED_MATTER_SHEARS)
				.pattern(" R")
				.pattern("S ")
				.define('R', PEItems.RED_MATTER)
				.define('S', PEItems.DARK_MATTER_SHEARS)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_SHEARS))
				.save(recipeOutput);
		//Hammer
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.RED_MATTER_HAMMER)
				.pattern("RMR")
				.pattern(" H ")
				.pattern(" M ")
				.define('R', PEItems.RED_MATTER)
				.define('M', PEItems.DARK_MATTER)
				.define('H', PEItems.DARK_MATTER_HAMMER)
				.unlockedBy("has_matter", hasItems(PEItems.RED_MATTER, PEItems.DARK_MATTER_HAMMER))
				.save(recipeOutput);
		//Katar (unlike the other recipes, any of the tools will work as a recipe unlock/showing)
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, PEItems.RED_MATTER_KATAR)
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
				.save(recipeOutput);
		//Morning Star (unlike the other recipes, any of the tools will work as a recipe unlock/showing)
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.RED_MATTER_MORNING_STAR)
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
				.save(recipeOutput);
	}

	private static void gemArmorRecipes(RecipeOutput recipeOutput) {
		//Helmet
		gemArmorRecipe(recipeOutput, () -> ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, PEItems.GEM_HELMET)
				.requires(PEItems.RED_MATTER_HELMET)
				.requires(PEItems.EVERTIDE_AMULET)
				.requires(PEItems.SOUL_STONE)
				.unlockedBy("has_helmet", has(PEItems.RED_MATTER_HELMET)), PEItems.GEM_HELMET);
		//Chestplate
		gemArmorRecipe(recipeOutput, () -> ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, PEItems.GEM_CHESTPLATE)
				.requires(PEItems.RED_MATTER_CHESTPLATE)
				.requires(PEItems.VOLCANITE_AMULET)
				.requires(PEItems.BODY_STONE)
				.unlockedBy("has_chestplate", has(PEItems.RED_MATTER_CHESTPLATE)), PEItems.GEM_CHESTPLATE);
		//Leggings
		gemArmorRecipe(recipeOutput, () -> ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, PEItems.GEM_LEGGINGS)
				.requires(PEItems.RED_MATTER_LEGGINGS)
				.requires(PEItems.BLACK_HOLE_BAND)
				.requires(PEItems.WATCH_OF_FLOWING_TIME)
				.unlockedBy("has_leggings", has(PEItems.RED_MATTER_LEGGINGS)), PEItems.GEM_LEGGINGS);
		//Boots
		gemArmorRecipe(recipeOutput, () -> ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, PEItems.GEM_BOOTS)
				.requires(PEItems.RED_MATTER_BOOTS)
				.requires(PEItems.SWIFTWOLF_RENDING_GALE, 2)
				.unlockedBy("has_boots", has(PEItems.RED_MATTER_BOOTS)), PEItems.GEM_BOOTS);
	}

	private static Ingredient getFullKleinStarIngredient(EnumKleinTier tier) {
		CompoundTag nbt = new CompoundTag();
		ItemPE.setEmc(nbt, Constants.MAX_KLEIN_EMC[tier.ordinal()]);
		return NBTIngredient.of(false, nbt, PEItems.getStar(tier));
	}

	private static void gemArmorRecipe(RecipeOutput recipeOutput, Supplier<ShapelessRecipeBuilder> builder, ItemRegistryObject<?> result) {
		//Full stars should be used
		builder.get()
				.requires(getFullKleinStarIngredient(EnumKleinTier.OMEGA))
				.save(recipeOutput.withConditions(FullKleinStarsCondition.INSTANCE), result.getId().withPrefix("full_star_"));
		//Full stars should not be used
		builder.get()
				.requires(PEItems.KLEIN_STAR_OMEGA)
				.save(recipeOutput.withConditions(new NotCondition(FullKleinStarsCondition.INSTANCE)), result.getId());
	}

	private static void fuelUpgradeRecipe(RecipeOutput recipeOutput, ItemLike input, ItemLike output) {
		String inputName = getName(input);
		String outputName = getName(output);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output)
				.requires(PEItems.PHILOSOPHERS_STONE)
				.requires(input, 4)
				.unlockedBy("has_" + inputName, hasItems(PEItems.PHILOSOPHERS_STONE, input))
				.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, input, 4)
				.requires(PEItems.PHILOSOPHERS_STONE)
				.requires(output)
				.unlockedBy("has_" + outputName, hasItems(PEItems.PHILOSOPHERS_STONE, output))
				.save(recipeOutput, PECore.rl("conversions/" + outputName + "_to_" + inputName));
	}

	private static void fuelBlockRecipes(RecipeOutput recipeOutput, ItemLike fuel, ItemLike block) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, block)
				.requires(fuel, 9)
				.unlockedBy("has_" + getName(fuel), has(fuel))
				.save(recipeOutput);
		String blockName = getName(block);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, fuel, 9)
				.requires(block)
				.unlockedBy("has_" + blockName, has(block))
				.save(recipeOutput, PECore.rl("conversions/" + blockName + "_deconstruct"));
	}

	private static void matterBlockRecipes(RecipeOutput recipeOutput, ItemLike matter, ItemLike block) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block)
				.pattern("MM")
				.pattern("MM")
				.define('M', matter)
				.unlockedBy("has_" + getName(matter), has(matter))
				.save(recipeOutput);
		String blockName = getName(block);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, matter, 4)
				.requires(block)
				.unlockedBy("has_" + blockName, has(block))
				.save(recipeOutput, PECore.rl("conversions/" + blockName + "_deconstruct"));
	}

	private static void addCollectorRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEBlocks.COLLECTOR)
				.pattern("GTG")
				.pattern("GDG")
				.pattern("GFG")
				.define('G', Items.GLOWSTONE)
				.define('F', Items.FURNACE)
				.define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.define('T', Items.GLASS)
				.unlockedBy("has_diamond", has(Tags.Items.STORAGE_BLOCKS_DIAMOND))
				.save(recipeOutput);
		addCollectorUpgradeRecipes(recipeOutput, PEBlocks.COLLECTOR_MK2, PEBlocks.COLLECTOR, PEItems.DARK_MATTER);
		addCollectorUpgradeRecipes(recipeOutput, PEBlocks.COLLECTOR_MK3, PEBlocks.COLLECTOR_MK2, PEItems.RED_MATTER);
	}

	private static void addCollectorUpgradeRecipes(RecipeOutput recipeOutput, ItemLike collector, ItemLike previous, ItemLike upgradeItem) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, collector)
				.pattern("GUG")
				.pattern("GPG")
				.pattern("GGG")
				.define('G', Items.GLOWSTONE)
				.define('P', previous)
				.define('U', upgradeItem)
				.unlockedBy("has_previous", has(previous))
				.save(recipeOutput);
	}

	private static void addRelayRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEBlocks.RELAY)
				.pattern("OSO")
				.pattern("ODO")
				.pattern("OOO")
				.define('S', Items.GLASS)
				.define('O', Tags.Items.OBSIDIAN)
				.define('D', Tags.Items.STORAGE_BLOCKS_DIAMOND)
				.unlockedBy("has_diamond", has(Tags.Items.STORAGE_BLOCKS_DIAMOND))
				.save(recipeOutput);
		addRelayUpgradeRecipes(recipeOutput, PEBlocks.RELAY_MK2, PEBlocks.RELAY, PEItems.DARK_MATTER);
		addRelayUpgradeRecipes(recipeOutput, PEBlocks.RELAY_MK3, PEBlocks.RELAY_MK2, PEItems.RED_MATTER);
	}

	private static void addRelayUpgradeRecipes(RecipeOutput recipeOutput, ItemLike relay, ItemLike previous, ItemLike upgradeItem) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, relay)
				.pattern("OUO")
				.pattern("OPO")
				.pattern("OOO")
				.define('O', Tags.Items.OBSIDIAN)
				.define('P', previous)
				.define('U', upgradeItem)
				.unlockedBy("has_previous", has(previous))
				.save(recipeOutput);
	}

	private static void addCondenserRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEBlocks.CONDENSER)
				.pattern("ODO")
				.pattern("DCD")
				.pattern("ODO")
				.define('C', PEBlocks.ALCHEMICAL_CHEST)
				.define('O', Tags.Items.OBSIDIAN)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_alchemical_chest", has(PEBlocks.ALCHEMICAL_CHEST))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEBlocks.CONDENSER_MK2)
				.pattern("RDR")
				.pattern("DCD")
				.pattern("RDR")
				.define('R', PEBlocks.RED_MATTER)
				.define('C', PEBlocks.CONDENSER)
				.define('D', PEBlocks.DARK_MATTER)
				.unlockedBy("has_previous", has(PEBlocks.CONDENSER))
				.save(recipeOutput);
	}

	private static void addFurnaceRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEBlocks.DARK_MATTER_FURNACE)
				.pattern("DDD")
				.pattern("DFD")
				.pattern("DDD")
				.define('D', PEBlocks.DARK_MATTER)
				.define('F', Items.FURNACE)
				.unlockedBy("has_dark_matter", has(PEBlocks.DARK_MATTER))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEBlocks.RED_MATTER_FURNACE)
				.pattern(" R ")
				.pattern("RFR")
				.define('R', PEBlocks.RED_MATTER)
				.define('F', PEBlocks.DARK_MATTER_FURNACE)
				.unlockedBy("has_previous", has(PEBlocks.DARK_MATTER_FURNACE))
				.save(recipeOutput);
	}

	private static void addKleinRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.KLEIN_STAR_EIN)
				.pattern("MMM")
				.pattern("MDM")
				.pattern("MMM")
				.define('M', PEItems.MOBIUS_FUEL)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_components", hasItems(PEItems.MOBIUS_FUEL, Tags.Items.GEMS_DIAMOND))
				.save(recipeOutput);
		EnumKleinTier[] tiers = EnumKleinTier.values();
		for (int tier = 1; tier < tiers.length; tier++) {
			kleinStarUpgrade(recipeOutput, PEItems.getStar(tiers[tier]), PEItems.getStar(tiers[tier - 1]));
		}
	}

	private static void kleinStarUpgrade(RecipeOutput recipeOutput, ItemLike star, ItemLike previous) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, star)
				.requires(previous, 4)
				.unlockedBy("has_components", has(previous))
				//Wrap the recipeOutput so that we can replace it with the proper serializer
				.save(new WrappingRecipeOutput(recipeOutput, recipe -> new RecipeShapelessKleinStar((ShapelessRecipe) recipe)));
	}

	private static void addRingRecipes(RecipeOutput recipeOutput) {
		//Arcana (Any ring or red matter)
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.ARCANA_RING)
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
				.save(recipeOutput);
		//Archangel Smite
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.ARCHANGEL_SMITE)
				.pattern("BFB")
				.pattern("MIM")
				.pattern("BFB")
				.define('B', Items.BOW)
				.define('F', Tags.Items.FEATHERS)
				.define('I', PEItems.IRON_BAND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(recipeOutput);
		//Black Hole Band
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.BLACK_HOLE_BAND)
				.pattern("SSS")
				.pattern("DID")
				.pattern("SSS")
				.define('S', Tags.Items.STRING)
				.define('D', PEItems.DARK_MATTER)
				.define('I', PEItems.IRON_BAND)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(recipeOutput);
		//Body Stone
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.BODY_STONE)
				.pattern("SSS")
				.pattern("RLR")
				.pattern("SSS")
				.define('R', PEItems.RED_MATTER)
				.define('S', Items.SUGAR)
				.define('L', Tags.Items.GEMS_LAPIS)
				.unlockedBy("has_matter", has(PEItems.RED_MATTER))
				.save(recipeOutput);
		//Harvest Goddess
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.HARVEST_GODDESS_BAND)
				.pattern("SFS")
				.pattern("DID")
				.pattern("SFS")
				.define('S', ItemTags.SAPLINGS)
				.define('D', PEItems.DARK_MATTER)
				.define('F', ItemTags.FLOWERS)
				.define('I', PEItems.IRON_BAND)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(recipeOutput);
		//Ignition
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.IGNITION_RING)
				.pattern("FMF")
				.pattern("DID")
				.pattern("FMF")
				.define('D', PEItems.DARK_MATTER)
				.define('F', Items.FLINT_AND_STEEL)
				.define('I', PEItems.IRON_BAND)
				.define('M', PEItems.MOBIUS_FUEL)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(recipeOutput);
		//Iron Band
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.IRON_BAND)
				.pattern("III")
				.pattern("ILI")
				.pattern("III")
				.define('I', Tags.Items.INGOTS_IRON)
				.define('L', Ingredient.of(Items.LAVA_BUCKET, PEItems.VOLCANITE_AMULET))
				.unlockedBy("has_lava", has(Items.LAVA_BUCKET))
				.unlockedBy("has_amulet", has(PEItems.VOLCANITE_AMULET))
				.save(recipeOutput);
		//Life Stone
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, PEItems.LIFE_STONE)
				.requires(PEItems.BODY_STONE)
				.requires(PEItems.SOUL_STONE)
				.unlockedBy("has_body", has(PEItems.BODY_STONE))
				.unlockedBy("has_soul", has(PEItems.SOUL_STONE))
				.save(recipeOutput);
		//Mind Stone
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.MIND_STONE)
				.pattern("BBB")
				.pattern("RLR")
				.pattern("BBB")
				.define('R', PEItems.RED_MATTER)
				.define('B', Items.BOOK)
				.define('L', Tags.Items.GEMS_LAPIS)
				.unlockedBy("has_matter", has(PEItems.RED_MATTER))
				.save(recipeOutput);
		//Soul Stone
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.SOUL_STONE)
				.pattern("GGG")
				.pattern("RLR")
				.pattern("GGG")
				.define('R', PEItems.RED_MATTER)
				.define('G', Tags.Items.DUSTS_GLOWSTONE)
				.define('L', Tags.Items.GEMS_LAPIS)
				.unlockedBy("has_matter", has(PEItems.RED_MATTER))
				.save(recipeOutput);
		//SWRG
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.SWIFTWOLF_RENDING_GALE)
				.pattern("DFD")
				.pattern("FIF")
				.pattern("DFD")
				.define('D', PEItems.DARK_MATTER)
				.define('F', Tags.Items.FEATHERS)
				.define('I', PEItems.IRON_BAND)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(recipeOutput);
		//Void Ring
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, PEItems.VOID_RING)
				.requires(PEItems.BLACK_HOLE_BAND)
				.requires(PEItems.GEM_OF_ETERNAL_DENSITY)
				.requires(PEItems.RED_MATTER, 2)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.unlockedBy("has_band", has(PEItems.BLACK_HOLE_BAND))
				.unlockedBy("has_gem", has(PEItems.GEM_OF_ETERNAL_DENSITY))
				.save(recipeOutput);
		//Watch of Flowing Time
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.WATCH_OF_FLOWING_TIME)
				.pattern("DGD")
				.pattern("OCO")
				.pattern("DGD")
				.define('C', Items.CLOCK)
				.define('D', PEItems.DARK_MATTER)
				.define('G', Items.GLOWSTONE)
				.define('O', Tags.Items.OBSIDIAN)
				.unlockedBy("has_matter", hasItems(PEItems.DARK_MATTER, Items.CLOCK))
				.save(recipeOutput);
		//Zero
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.ZERO_RING)
				.pattern("SBS")
				.pattern("MIM")
				.pattern("SBS")
				.define('B', Items.SNOWBALL)
				.define('S', Items.SNOW_BLOCK)
				.define('I', PEItems.IRON_BAND)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(recipeOutput);
	}

	private static void addCovalenceDustRecipes(RecipeOutput recipeOutput) {
		ShapelessRecipeBuilder lowCovalenceDust = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PEItems.LOW_COVALENCE_DUST, 40)
				.requires(Items.CHARCOAL)
				.unlockedBy("has_cobble", has(Tags.Items.COBBLESTONE_NORMAL));
		for (int i = 0; i < 8; i++) {
			lowCovalenceDust.requires(Tags.Items.COBBLESTONE_NORMAL);
		}
		lowCovalenceDust.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PEItems.MEDIUM_COVALENCE_DUST, 40)
				.requires(Tags.Items.INGOTS_IRON)
				.requires(Tags.Items.DUSTS_REDSTONE)
				.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
				.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, PEItems.HIGH_COVALENCE_DUST, 40)
				.requires(Tags.Items.GEMS_DIAMOND)
				.requires(Items.COAL)
				.unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
				.save(recipeOutput);
	}

	private static void addDiviningRodRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.LOW_DIVINING_ROD)
				.pattern("DDD")
				.pattern("DSD")
				.pattern("DDD")
				.define('S', Tags.Items.RODS_WOODEN)
				.define('D', PEItems.LOW_COVALENCE_DUST)
				.unlockedBy("has_covalence_dust", has(PEItems.LOW_COVALENCE_DUST))
				.save(recipeOutput);
		diviningRodRecipe(recipeOutput, PEItems.MEDIUM_DIVINING_ROD, PEItems.LOW_DIVINING_ROD, PEItems.MEDIUM_COVALENCE_DUST);
		diviningRodRecipe(recipeOutput, PEItems.HIGH_DIVINING_ROD, PEItems.MEDIUM_DIVINING_ROD, PEItems.HIGH_COVALENCE_DUST);
	}

	private static void diviningRodRecipe(RecipeOutput recipeOutput, ItemLike rod, ItemLike previous, ItemLike covalence) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, rod)
				.pattern("DDD")
				.pattern("DSD")
				.pattern("DDD")
				.define('S', previous)
				.define('D', covalence)
				.unlockedBy("has_previous", has(previous))
				.save(recipeOutput);
	}

	private static void addMiscToolRecipes(RecipeOutput recipeOutput) {
		//Catalytic lens
		catalyticLensRecipe(recipeOutput, false);
		catalyticLensRecipe(recipeOutput, true);
		//Destruction Catalyst
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.DESTRUCTION_CATALYST)
				.pattern("NMN")
				.pattern("MFM")
				.pattern("NMN")
				.define('F', Items.FLINT_AND_STEEL)
				.define('M', PEItems.MOBIUS_FUEL)
				.define('N', PEBlocks.NOVA_CATALYST)
				.unlockedBy("has_catalyst", has(PEBlocks.NOVA_CATALYST))
				.save(recipeOutput);
		//Evertide Amulet
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.EVERTIDE_AMULET)
				.pattern("WWW")
				.pattern("DDD")
				.pattern("WWW")
				.define('D', PEItems.DARK_MATTER)
				.define('W', Items.WATER_BUCKET)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(recipeOutput);
		//Gem of Eternal Density
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.GEM_OF_ETERNAL_DENSITY)
				.pattern("DOD")
				.pattern("MDM")
				.pattern("DOD")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.define('O', Tags.Items.OBSIDIAN)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(recipeOutput);
		//Hyperkinetic Lens
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.HYPERKINETIC_LENS)
				.pattern("DDD")
				.pattern("MNM")
				.pattern("DDD")
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('M', PEItems.DARK_MATTER)
				.define('N', PEBlocks.NOVA_CATALYST)
				.unlockedBy("has_catalyst", has(PEBlocks.NOVA_CATALYST))
				.save(recipeOutput);
		//Mercurial Eye
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.MERCURIAL_EYE)
				.pattern("OBO")
				.pattern("BRB")
				.pattern("BDB")
				.define('B', Items.BRICKS)
				.define('R', PEItems.RED_MATTER)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.define('O', Tags.Items.OBSIDIAN)
				.unlockedBy("has_matter", has(PEBlocks.RED_MATTER))
				.save(recipeOutput);
		//Philosopher's Stone
		philosopherStoneRecipe(recipeOutput, false);
		philosopherStoneRecipe(recipeOutput, true);
		//Repair Talisman
		repairTalismanRecipe(recipeOutput, false);
		repairTalismanRecipe(recipeOutput, true);
		//Volcanite Amulet
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.VOLCANITE_AMULET)
				.pattern("LLL")
				.pattern("DDD")
				.pattern("LLL")
				.define('D', PEItems.DARK_MATTER)
				.define('L', Items.LAVA_BUCKET)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.save(recipeOutput);
	}

	private static void catalyticLensRecipe(RecipeOutput recipeOutput, boolean alternate) {
		String name = PEItems.CATALYTIC_LENS.getId().toString();
		ShapedRecipeBuilder lens = ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.CATALYTIC_LENS)
				.pattern("MMM")
				.pattern(alternate ? "HMD" : "DMH")
				.pattern("MMM")
				.define('D', PEItems.DESTRUCTION_CATALYST)
				.define('H', PEItems.HYPERKINETIC_LENS)
				.define('M', PEItems.DARK_MATTER)
				.unlockedBy("has_matter", has(PEItems.DARK_MATTER))
				.group(name);
		if (alternate) {
			lens.save(recipeOutput, name + "_alt");
		} else {
			lens.save(recipeOutput);
		}
	}

	private static void philosopherStoneRecipe(RecipeOutput recipeOutput, boolean alternate) {
		String name = PEItems.PHILOSOPHERS_STONE.getId().toString();
		ShapedRecipeBuilder philoStone = ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.PHILOSOPHERS_STONE)
				.define('R', Tags.Items.DUSTS_REDSTONE)
				.define('G', Tags.Items.DUSTS_GLOWSTONE)
				.define('D', Tags.Items.GEMS_DIAMOND)
				.unlockedBy("has_glowstone", has(Tags.Items.DUSTS_GLOWSTONE))
				.group(name);
		if (alternate) {
			philoStone.pattern("GRG")
					.pattern("RDR")
					.pattern("GRG")
					.save(recipeOutput, name + "_alt");
		} else {
			philoStone.pattern("RGR")
					.pattern("GDG")
					.pattern("RGR")
					.save(recipeOutput);
		}
	}

	private static void repairTalismanRecipe(RecipeOutput recipeOutput, boolean alternate) {
		String lowToHigh = "LMH";
		String highToLow = "HML";
		String name = PEItems.REPAIR_TALISMAN.getId().toString();
		ShapedRecipeBuilder talisman = ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, PEItems.REPAIR_TALISMAN)
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
			talisman.save(recipeOutput, name + "_alt");
		} else {
			talisman.save(recipeOutput);
		}
	}

	private static void addTransmutationTableRecipes(RecipeOutput recipeOutput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEBlocks.TRANSMUTATION_TABLE)
				.pattern("OSO")
				.pattern("SPS")
				.pattern("OSO")
				.define('S', Tags.Items.STONE)
				.define('O', Tags.Items.OBSIDIAN)
				.define('P', PEItems.PHILOSOPHERS_STONE)
				.unlockedBy("has_philo_stone", has(PEItems.PHILOSOPHERS_STONE))
				.save(recipeOutput);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, PEItems.TRANSMUTATION_TABLET)
				.pattern("DSD")
				.pattern("STS")
				.pattern("DSD")
				.define('S', Tags.Items.STONE)
				.define('D', PEBlocks.DARK_MATTER)
				.define('T', PEBlocks.TRANSMUTATION_TABLE)
				.unlockedBy("has_table", has(PEBlocks.TRANSMUTATION_TABLE))
				.save(recipeOutput);
	}

	private static void addNovaRecipes(RecipeOutput recipeOutput) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, PEBlocks.NOVA_CATALYST, 2)
				.requires(Items.TNT)
				.requires(PEItems.MOBIUS_FUEL)
				.unlockedBy("has_tnt", has(Items.TNT))
				.save(recipeOutput);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, PEBlocks.NOVA_CATACLYSM, 2)
				.requires(PEBlocks.NOVA_CATALYST)
				.requires(PEItems.AETERNALIS_FUEL)
				.unlockedBy("has_catalyst", has(PEBlocks.NOVA_CATALYST))
				.save(recipeOutput);
	}

	private static void addBagRecipes(RecipeOutput recipeOutput) {
		Criterion<InventoryChangeTrigger.TriggerInstance> hasChest = has(PEBlocks.ALCHEMICAL_CHEST);
		Criterion<InventoryChangeTrigger.TriggerInstance> hasBag = has(PETags.Items.ALCHEMICAL_BAGS);
		for (DyeColor color : DyeColor.values()) {
			ItemRegistryObject<AlchemicalBag> bag = PEItems.getBagReference(color);
			//Crafting recipe
			ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, bag)
					.pattern("CCC")
					.pattern("WAW")
					.pattern("WWW")
					.define('A', PEBlocks.ALCHEMICAL_CHEST)
					.define('C', PEItems.HIGH_COVALENCE_DUST)
					.define('W', getWool(color))
					.unlockedBy("has_alchemical_chest", hasChest)
					.save(recipeOutput);
			//Dye bag conversion recipes
			ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, bag)
					.requires(PETags.Items.ALCHEMICAL_BAGS)
					.requires(color.getTag())
					.unlockedBy("has_alchemical_bag", hasBag)
					.save(recipeOutput, PECore.rl("conversions/dye_bag_" + color));
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

	private static void addConversionRecipes(RecipeOutput recipeOutput) {
		philoConversionRecipe(recipeOutput, Items.CHARCOAL, 4, Items.COAL, 1);
		philoConversionRecipe(recipeOutput, Tags.Items.GEMS_DIAMOND, Items.DIAMOND, 2, Tags.Items.GEMS_EMERALD, Items.EMERALD, 1);
		philoConversionRecipe(recipeOutput, Tags.Items.INGOTS_GOLD, Items.GOLD_INGOT, 4, Tags.Items.GEMS_DIAMOND, Items.DIAMOND, 1);
		philoConversionRecipe(recipeOutput, Tags.Items.INGOTS_IRON, Items.IRON_INGOT, 8, Tags.Items.INGOTS_GOLD, Items.GOLD_INGOT, 1);
		//Iron -> Ender Pearl
		philoConversionRecipe(recipeOutput, getName(Items.IRON_INGOT), Tags.Items.INGOTS_IRON, 4, getName(Items.ENDER_PEARL), Items.ENDER_PEARL, 1);
		//Dirt -> Grass
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GRASS_BLOCK)
				.requires(PEItems.ARCANA_RING)
				.requires(Items.DIRT)
				.unlockedBy("has_arcana_ring", has(PEItems.ARCANA_RING))
				.save(recipeOutput, PECore.rl("conversions/dirt_to_grass"));
		//Redstone -> Lava
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LAVA_BUCKET)
				.requires(PEItems.VOLCANITE_AMULET)
				.requires(Items.BUCKET)
				.requires(Tags.Items.DUSTS_REDSTONE)
				.unlockedBy("has_volcanite_amulet", has(PEItems.VOLCANITE_AMULET))
				.save(recipeOutput, PECore.rl("conversions/redstone_to_lava"));
		//Water -> Ice
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.ICE)
				.requires(Ingredient.of(PEItems.ARCANA_RING, PEItems.ZERO_RING))
				.requires(Ingredient.of(Items.WATER_BUCKET, PEItems.EVERTIDE_AMULET))
				.unlockedBy("has_arcana_ring", has(PEItems.ARCANA_RING))
				.unlockedBy("has_zero_ring", has(PEItems.ZERO_RING))
				.save(recipeOutput, PECore.rl("conversions/water_to_ice"));
	}

	private static void philoConversionRecipe(RecipeOutput recipeOutput, ItemLike a, int aAmount, ItemLike b, int bAmount) {
		String aName = getName(a);
		String bName = getName(b);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, b, bAmount)
				.requires(PEItems.PHILOSOPHERS_STONE)
				.requires(a, aAmount)
				.unlockedBy("has_" + aName, hasItems(PEItems.PHILOSOPHERS_STONE, a))
				.save(recipeOutput, PECore.rl("conversions/" + aName + "_to_" + bName));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, a, aAmount)
				.requires(PEItems.PHILOSOPHERS_STONE)
				.requires(b, bAmount)
				.unlockedBy("has_" + bName, hasItems(PEItems.PHILOSOPHERS_STONE, b))
				.save(recipeOutput, PECore.rl("conversions/" + bName + "_to_" + aName));
	}

	private static void philoConversionRecipe(RecipeOutput recipeOutput, TagKey<Item> aTag, ItemLike a, int aAmount, TagKey<Item> bTag, ItemLike b,
			int bAmount) {
		String aName = getName(a);
		String bName = getName(b);
		//A to B
		philoConversionRecipe(recipeOutput, aName, aTag, aAmount, bName, b, bAmount);
		//B to A
		philoConversionRecipe(recipeOutput, bName, bTag, bAmount, aName, a, aAmount);
	}

	private static void philoConversionRecipe(RecipeOutput recipeOutput, String inputName, TagKey<Item> inputTag, int inputAmount, String outputName,
			ItemLike output, int outputAmount) {
		ShapelessRecipeBuilder bToA = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, outputAmount)
				.requires(PEItems.PHILOSOPHERS_STONE)
				.unlockedBy("has_" + inputName, hasItems(PEItems.PHILOSOPHERS_STONE, inputTag));
		for (int i = 0; i < inputAmount; i++) {
			bToA.requires(inputTag);
		}
		bToA.save(recipeOutput, PECore.rl("conversions/" + inputName + "_to_" + outputName));
	}

	private static String getName(ItemLike item) {
		return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemLike... items) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(items);
	}

	@SafeVarargs
	protected static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemLike item, TagKey<Item>... tags) {
		return hasItems(new ItemLike[]{item}, tags);
	}

	@SafeVarargs
	protected static Criterion<InventoryChangeTrigger.TriggerInstance> hasItems(ItemLike[] items, TagKey<Item>... tags) {
		ItemPredicate[] predicates = new ItemPredicate[items.length + tags.length];
		for (int i = 0; i < items.length; ++i) {
			predicates[i] = ItemPredicate.Builder.item().of(items[i]).build();
		}
		for (int i = 0; i < tags.length; ++i) {
			predicates[items.length + i] = ItemPredicate.Builder.item().of(tags[i]).build();
		}
		return inventoryTrigger(predicates);
	}

	private record WrappingRecipeOutput(RecipeOutput parent, UnaryOperator<Recipe<?>> recipeWrapper) implements RecipeOutput {

		@Override
		public void accept(ResourceLocation recipeId, Recipe<?> recipe, @Nullable AdvancementHolder advancementHolder, ICondition... conditions) {
			parent.accept(recipeId, recipe, advancementHolder, conditions);
		}

		@NotNull
		@Override
		public Advancement.Builder advancement() {
			return parent.advancement();
		}
	}
}