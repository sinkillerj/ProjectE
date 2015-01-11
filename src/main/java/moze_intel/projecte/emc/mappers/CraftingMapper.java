package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;

public class CraftingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	List<IRecipeMapper> recipeMappers = Arrays.asList(new VanillaRecipeMapper(), new VanillaOreRecipeMapper());
	Set<Class> canNotMap = new HashSet<Class>();

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper) {
		Iterator<IRecipe> iter = CraftingManager.getInstance().getRecipeList().iterator();
		while (iter.hasNext()) {
			IRecipe recipe = iter.next();
			boolean handled = false;
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput == null) continue;
			NormalizedSimpleStack recipeOutputNorm = NormalizedSimpleStack.getNormalizedSimpleStackFor(recipeOutput);
			for (IRecipeMapper recipeMapper : recipeMappers) {
				if (recipeMapper.canHandle(recipe)) {
					handled = true;
					Iterable<Iterable<ItemStack>> ingredientMultipleVariantions = recipeMapper.getIngredientsFor(recipe);
					if (ingredientMultipleVariantions != null) {
						for (Iterable<ItemStack> variation : ingredientMultipleVariantions) {
							IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<NormalizedSimpleStack>();
							for (ItemStack stack : variation) {
								if (stack == null || stack.getItem() == null) continue;
								if (stack.getItem().doesContainerItemLeaveCraftingGrid(stack)) {
									if (stack.getItem().hasContainerItem(stack)) {
										ingredientMap.addIngredient(NormalizedSimpleStack.getNormalizedSimpleStackFor(stack.getItem().getContainerItem(stack)), -1);
									}
									ingredientMap.addIngredient(NormalizedSimpleStack.getNormalizedSimpleStackFor(stack), 1);
								}
							}
							if (recipeOutput.stackSize > 0) {
								mapper.addConversionMultiple(recipeOutput.stackSize, recipeOutputNorm, ingredientMap.getMap());
							} else {
								PELogger.logWarn("Ignoring Recipe because outnumber <= 0: " + ingredientMap.getMap().toString() + " -> " + recipeOutput);
							}
						}
					} else {
						PELogger.logWarn("RecipeMapper " + recipeMapper + " failed to map Recipe" + recipe);
					}
					break;
				}
			}
			if (!handled) {
				if (!canNotMap.contains(recipe.getClass())) {
					canNotMap.add(recipe.getClass());
					PELogger.logWarn("Can not map Crafting Recipes with Type: " + recipe.getClass().getName());
				}
			}
		}
	}


	protected static interface IRecipeMapper {
		public boolean canHandle(IRecipe recipe);

		public Iterable<Iterable<ItemStack>> getIngredientsFor(IRecipe recipe);
	}

	//TODO implement IRecipeMapper for ShapedRecipes, ShapelessRecipes, ShapedOreRecipe, ShapelessOreRecipe
	protected static class VanillaRecipeMapper implements IRecipeMapper {

		@Override
		public boolean canHandle(IRecipe recipe) {
			return recipe instanceof ShapedRecipes || recipe instanceof ShapelessRecipes;
		}

		@Override
		public Iterable<Iterable<ItemStack>> getIngredientsFor(IRecipe recipe) {
			Iterable recipeItems = null;
			if (recipe instanceof ShapedRecipes) {
				recipeItems = Arrays.asList(((ShapedRecipes) recipe).recipeItems);
			} else if (recipe instanceof ShapelessRecipes) {
				recipeItems = ((ShapelessRecipes) recipe).recipeItems;
			}
			List<ItemStack> inputs = new LinkedList<ItemStack>();
			for (Object o : recipeItems) {
				if (o == null) continue;
				if (o instanceof ItemStack) {
					ItemStack recipeItem = (ItemStack) o;
					inputs.add(recipeItem);
				} else {
					PELogger.logWarn("Illegal Ingredient in Crafting Recipe: " + o.toString());
				}
			}
			return Arrays.asList((Iterable<ItemStack>) inputs);
		}

	}

	public static <T> Iterable<Iterable<T>> recursiveRecipeInput(Iterable<T> recipeInputFixed, List<Iterable<T>> recipeInputsWithOptions) {
		List<Iterable<T>> out = new ArrayList<Iterable<T>>();
		Stack s = new Stack<T>();
		for (T fixedInput : recipeInputFixed)
			s.add(fixedInput);
		recursiveRecipeInput(recipeInputsWithOptions, 0, out, s);
		return out;
	}

	public static <T> void recursiveRecipeInput(List<Iterable<T>> objects, int index, List<Iterable<T>> out, Stack<T> currentIngredients) {
		if (index < objects.size()) {
			Iterable<T> next = objects.get(index);
			for (T o : next) {
				currentIngredients.push(o);
				recursiveRecipeInput(objects, index + 1, out, currentIngredients);
				currentIngredients.pop();
			}
		} else if (index == objects.size()) {
			List recipeInput = new LinkedList();
			for (T is : currentIngredients) {
				recipeInput.add(is);
			}
			out.add(recipeInput);
		}
	}

	protected static class VanillaOreRecipeMapper implements IRecipeMapper {

		@Override
		public boolean canHandle(IRecipe recipe) {
			return recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessOreRecipe;
		}

		@Override
		public Iterable<Iterable<ItemStack>> getIngredientsFor(IRecipe recipe) {
			List<IngredientMap<ItemStack>> inputs = new LinkedList<IngredientMap<ItemStack>>();
			Iterable<Object> recipeItems = null;
			if (recipe instanceof ShapedOreRecipe) {
				recipeItems = Arrays.asList(((ShapedOreRecipe) recipe).getInput());
			} else if (recipe instanceof ShapelessOreRecipe) {
				recipeItems = ((ShapelessOreRecipe) recipe).getInput();
			}
			if (recipeItems == null) return null;
			ArrayList<Iterable<ItemStack>> variableInputs = new ArrayList<Iterable<ItemStack>>();
			ArrayList<ItemStack> fixedInputs = new ArrayList<ItemStack>();
			for (Object recipeItem : recipeItems) {
				if (recipeItem instanceof ItemStack) {
					fixedInputs.add((ItemStack) recipeItem);
				} else if (recipeItem instanceof Iterable) {
					List<ItemStack> recipeItemOptions = new LinkedList<ItemStack>();
					for (Object option : (Iterable) recipeItem) {
						if (option instanceof ItemStack) {
							recipeItemOptions.add((ItemStack) option);
						} else {
							PELogger.logWarn("Can not map recipe " + recipe + " because found " + option.toString() + " instead of ItemStack");
							return null;
						}
					}
					variableInputs.add(recipeItemOptions);
				}
			}
			return recursiveRecipeInput(fixedInputs, variableInputs);
		}
	}
}
