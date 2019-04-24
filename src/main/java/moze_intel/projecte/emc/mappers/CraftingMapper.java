package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.json.NSSFake;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessKleinStar;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CraftingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	private final List<IRecipeMapper> recipeMappers = Arrays.asList(new VanillaRecipeMapper(), new PECustomRecipeMapper(), new CraftTweakerRecipeMapper(), new RecipeStagesRecipeMapper());
	private final Set<Class> canNotMap = new HashSet<>();
	private final Map<Class, Integer> recipeCount = new HashMap<>();

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final Configuration config) {
		recipeCount.clear();
		canNotMap.clear();
		nextRecipe: for (IRecipe recipe : CraftingManager.REGISTRY) {
			boolean handled = false;
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput.isEmpty()) continue;
			NormalizedSimpleStack recipeOutputNorm = NSSItem.create(recipeOutput);
			for (IRecipeMapper recipeMapper : recipeMappers) {
				if (!config.getBoolean("enable" + recipeMapper.getName(), "IRecipeImplementations", true, recipeMapper.getDescription()))
					continue;
				if (recipeMapper.canHandle(recipe)) {
					handled = true;
					for (CraftingIngredients variation : recipeMapper.getIngredientsFor(recipe)) {
						IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
						for (ItemStack stack : variation.fixedIngredients) {
							if (stack.isEmpty()) continue;
							try {
								if (stack.getItemDamage() != OreDictionary.WILDCARD_VALUE && stack.getItem().hasContainerItem(stack)) {
									ingredientMap.addIngredient(NSSItem.create(stack.getItem().getContainerItem(stack)), -1);
								}
								ingredientMap.addIngredient(NSSItem.create(stack), 1);
							} catch (Exception e) {
								PECore.LOGGER.fatal("Exception in CraftingMapper when parsing Recipe Ingredients: RecipeType: {}, Ingredient: {}", recipe.getClass().getName(), stack.toString());
								e.printStackTrace();
								continue nextRecipe;
							}
						}
						for (Iterable<ItemStack> multiIngredient : variation.multiIngredients) {
							NormalizedSimpleStack dummy = NSSFake.create(multiIngredient.toString());
							ingredientMap.addIngredient(dummy, 1);
							for (ItemStack stack : multiIngredient) {
								if (stack.isEmpty()) continue;
								IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
								if (stack.getItem().hasContainerItem(stack)) {
									groupIngredientMap.addIngredient(NSSItem.create(stack.getItem().getContainerItem(stack)), -1);
								}
								groupIngredientMap.addIngredient(NSSItem.create(stack), 1);
								mapper.addConversion(1, dummy, groupIngredientMap.getMap());
							}
						}
						mapper.addConversion(recipeOutput.getCount(), recipeOutputNorm, ingredientMap.getMap());
					}
					break;
				}
			}
			if (!handled) {
				if (canNotMap.add(recipe.getClass())) {
					PECore.debugLog("Can not map Crafting Recipes with Type: {}", recipe.getClass().getName());
				}
			} else {
				int count = 0;
				if (recipeCount.containsKey(recipe.getClass())) {
					count = recipeCount.get(recipe.getClass());
				}
				count += 1;
				recipeCount.put(recipe.getClass(), count);
			}
		}

		PECore.debugLog("CraftingMapper Statistics:");
		for (Map.Entry<Class, Integer> entry: recipeCount.entrySet()) {
			PECore.debugLog("Found {} Recipes of Type {}", entry.getValue(), entry.getKey());
		}
	}

	@Override
	public String getName() {
		return "CraftingMapper";
	}

	@Override
	public String getDescription() {
		return "Add Conversions for Crafting Recipes gathered from net.minecraft.item.crafting.CraftingManager";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	public interface IRecipeMapper {
		String getName();
		String getDescription();
		boolean canHandle(IRecipe recipe);

		default Iterable<CraftingIngredients> getIngredientsFor(IRecipe recipe) {
			List<Iterable<ItemStack>> variableInputs = new ArrayList<>();
			List<ItemStack> fixedInputs = new ArrayList<>();
			for (Ingredient recipeItem : recipe.getIngredients()) {
				ItemStack[] matches = recipeItem.getMatchingStacks();
				if (matches.length == 1) {
					fixedInputs.add(matches[0].copy());
				} else if (matches.length > 0) {
					List<ItemStack> recipeItemOptions = new LinkedList<>();
					for (ItemStack option : matches) {
						recipeItemOptions.add(option.copy());
					}
					variableInputs.add(recipeItemOptions);
				}
			}
			return Collections.singletonList(new CraftingIngredients(fixedInputs, variableInputs));
		}
	}

	private static class CraftingIngredients {
		public final Iterable<ItemStack> fixedIngredients;
		public final Iterable<Iterable<ItemStack>> multiIngredients;
		public CraftingIngredients( Iterable<ItemStack> fixedIngredients, Iterable<Iterable<ItemStack>> multiIngredients) {
			this.fixedIngredients = fixedIngredients;
			this.multiIngredients = multiIngredients;
		}
	}

	private static class VanillaRecipeMapper implements IRecipeMapper {

		@Override
		public String getName() {
			return "VanillaRecipeMapper";
		}

		@Override
		public String getDescription() {
			return "Maps `IRecipe` crafting recipes that extend `ShapedRecipes` or `ShapelessRecipes`, and their oredict equivalents";
		}

		@Override
		public boolean canHandle(IRecipe recipe) {
			return recipe instanceof ShapedRecipes || recipe instanceof ShapelessRecipes || recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessOreRecipe;
		}
	}

	private static class PECustomRecipeMapper implements IRecipeMapper {

		@Override
		public String getName() {
			return "PECustomRecipeMapper";
		}

		@Override
		public String getDescription() {
			return "Maps custom IRecipe's from ProjectE";
		}

		@Override
		public boolean canHandle(IRecipe recipe) {
			return recipe instanceof RecipeShapelessKleinStar || recipe instanceof RecipeShapelessHidden;
		}
	}
}
