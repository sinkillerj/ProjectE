package moze_intel.projecte.emc.mappers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapedKleinStar;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CraftingMapper implements IEMCMapper<NormalizedSimpleStack, Integer> {

	List<IRecipeMapper> recipeMappers = Arrays.asList(new VanillaRecipeMapper(), new VanillaOreRecipeMapper(), new PECustomRecipeMapper());
	Set<Class> canNotMap = Sets.newHashSet();
	Map<Class, Integer> recipeCount = Maps.newHashMap();

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, final Configuration config) {
		recipeCount.clear();
		canNotMap.clear();
		recipeloop: for (IRecipe recipe : (Iterable<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
			boolean handled = false;
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput == null) continue;
			NormalizedSimpleStack recipeOutputNorm = NormalizedSimpleStack.getFor(recipeOutput);
			for (IRecipeMapper recipeMapper : recipeMappers) {
				if (!config.getBoolean("enable" + recipeMapper.getName(), "IRecipeImplementations", true, recipeMapper.getDescription()))
					continue;
				if (recipeMapper.canHandle(recipe)) {
					handled = true;
					Iterable<CraftingIngredients> craftingIngredientIterable = recipeMapper.getIngredientsFor(recipe);
					if (craftingIngredientIterable != null) {
						for (CraftingIngredients variation : craftingIngredientIterable) {
							IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
							for (ItemStack stack : variation.fixedIngredients) {
								if (stack == null || stack.getItem() == null) continue;
								if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
									//Don't check for doesContainerItemLeaveCraftingGrid for WILDCARD-ItemStacks
									ingredientMap.addIngredient(NormalizedSimpleStack.getFor(stack), 1);
								} else {
									//stack does not have a wildcard damage value
									try
									{
										if (stack.getItem().doesContainerItemLeaveCraftingGrid(stack))
										{
											if (stack.getItem().hasContainerItem(stack))
											{
												ingredientMap.addIngredient(NormalizedSimpleStack.getFor(stack.getItem().getContainerItem(stack)), -1);
											}
											ingredientMap.addIngredient(NormalizedSimpleStack.getFor(stack), 1);
										}
										else if (config.getBoolean("emcDependencyForUnconsumedItems", "", true, "If this option is enabled items that are made by crafting, with unconsumed ingredients, should only get an emc value, if the unconsumed item also has a value. (Examples: Extra Utilities Sigil, Cutting Board, Mixer, Juicer...)"))
										{
											//Container Item does not leave the crafting grid: we add an EMC dependency anyway.
											ingredientMap.addIngredient(NormalizedSimpleStack.getFor(stack), 0);
										}
									} catch (Exception e) {
										PELogger.logFatal("Exception in CraftingMapper when parsing Recipe Ingredients: RecipeType: %s, Ingredient: %s", recipe.getClass().getName(), stack.toString());
										e.printStackTrace();
										continue recipeloop;
									}
								}
							}
							for (Iterable<ItemStack> multiIngredient : variation.multiIngredients) {
								NormalizedSimpleStack normalizedSimpleStack = NormalizedSimpleStack.createFake(multiIngredient.toString());
								ingredientMap.addIngredient(normalizedSimpleStack, 1);
								for (ItemStack stack : multiIngredient) {
									if (stack == null || stack.getItem() == null) continue;
									if (stack.getItem().doesContainerItemLeaveCraftingGrid(stack)) {
										IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
										if (stack.getItem().hasContainerItem(stack)) {
											groupIngredientMap.addIngredient(NormalizedSimpleStack.getFor(stack.getItem().getContainerItem(stack)), -1);
										}
										groupIngredientMap.addIngredient(NormalizedSimpleStack.getFor(stack), 1);
										mapper.addConversion(1, normalizedSimpleStack, groupIngredientMap.getMap());
									}
								}
							}
							if (recipeOutput.stackSize > 0) {
								mapper.addConversion(recipeOutput.stackSize, recipeOutputNorm, ingredientMap.getMap());
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
			} else {
				int count = 0;
				if (recipeCount.containsKey(recipe.getClass())) {
					count = recipeCount.get(recipe.getClass());
				}
				count += 1;
				recipeCount.put(recipe.getClass(), count);
			}
		}

		PELogger.logInfo("CraftingMapper Statistics:");
		for (Map.Entry<Class, Integer> entry: recipeCount.entrySet()) {
			PELogger.logInfo(String.format("Found %d Recipes of Type %s", entry.getValue(), entry.getKey()));
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

	public static interface IRecipeMapper {
		public String getName();
		public String getDescription();
		public boolean canHandle(IRecipe recipe);

		public Iterable<CraftingIngredients> getIngredientsFor(IRecipe recipe);
	}

	public static class CraftingIngredients {
		public Iterable<ItemStack> fixedIngredients;
		public Iterable<Iterable<ItemStack>> multiIngredients;
		public CraftingIngredients( Iterable<ItemStack> fixedIngredients, Iterable<Iterable<ItemStack>> multiIngredients) {
			this.fixedIngredients = fixedIngredients;
			this.multiIngredients = multiIngredients;
		}
	}

	protected static class VanillaRecipeMapper implements IRecipeMapper {

		@Override
		public String getName() {
			return "VanillaRecipeMapper";
		}

		@Override
		public String getDescription() {
			return "Maps `IRecipe` crafting recipes that extend `ShapedRecipes` or `ShapelessRecipes`";
		}

		@Override
		public boolean canHandle(IRecipe recipe) {
			return recipe instanceof ShapedRecipes || recipe instanceof ShapelessRecipes;
		}

		@Override
		public Iterable<CraftingIngredients> getIngredientsFor(IRecipe recipe) {
			Iterable recipeItems = null;
			if (recipe instanceof ShapedRecipes) {
				recipeItems = Arrays.asList(((ShapedRecipes) recipe).recipeItems);
			} else if (recipe instanceof ShapelessRecipes) {
				recipeItems = ((ShapelessRecipes) recipe).recipeItems;
			}
			List<ItemStack> inputs = new LinkedList<>();
			for (Object o : recipeItems) {
				if (o == null) continue;
				if (o instanceof ItemStack) {
					ItemStack recipeItem = (ItemStack) o;
					inputs.add(recipeItem.copy());
				} else {
					PELogger.logWarn("Illegal Ingredient in Crafting Recipe: " + o.toString());
				}
			}
			return Arrays.asList(new CraftingIngredients(inputs, new LinkedList()));
		}

	}

	protected static class VanillaOreRecipeMapper implements IRecipeMapper {

		@Override
		public String getName() {
			return "VanillaOreRecipeMapper";
		}

		@Override
		public String getDescription() {
			return "Maps `IRecipe` crafting recipes that extend `ShapedOreRecipe` or `ShapelessOreRecipe`. This includes CraftingRecipes that use OreDictionary ingredients.";
		}

		@Override
		public boolean canHandle(IRecipe recipe) {
			return recipe instanceof  ShapedOreRecipe || recipe instanceof ShapelessOreRecipe;
		}

		@Override
		public Iterable<CraftingIngredients> getIngredientsFor(IRecipe recipe) {
			List<IngredientMap<ItemStack>> inputs = new LinkedList<>();
			Iterable<Object> recipeItems = null;
			if (recipe instanceof ShapedOreRecipe) {
				recipeItems = Arrays.asList(((ShapedOreRecipe) recipe).getInput());
			} else if (recipe instanceof ShapelessOreRecipe) {
				recipeItems = ((ShapelessOreRecipe) recipe).getInput();
			}
			if (recipeItems == null) return null;
			ArrayList<Iterable<ItemStack>> variableInputs = Lists.newArrayList();
			ArrayList<ItemStack> fixedInputs = Lists.newArrayList();
			for (Object recipeItem : recipeItems) {
				if (recipeItem instanceof ItemStack) {
					fixedInputs.add((ItemStack) recipeItem);
				} else if (recipeItem instanceof Collection) {
					List<ItemStack> recipeItemOptions = new LinkedList<>();
					Collection recipeItemCollection = ((Collection) recipeItem);
					if (recipeItemCollection.size() == 1) {
						Object element = recipeItemCollection.iterator().next();
						if (element instanceof ItemStack) {
							fixedInputs.add(((ItemStack) element).copy());
						} else {
							PELogger.logWarn("Can not map recipe " + recipe + " because found " + element.toString() + " instead of ItemStack");
							return null;
						}
						continue;
					}
					for (Object option : recipeItemCollection) {
						if (option instanceof ItemStack) {
							recipeItemOptions.add(((ItemStack) option).copy());
						} else {
							PELogger.logWarn("Can not map recipe " + recipe + " because found " + option.toString() + " instead of ItemStack");
							return null;
						}
					}
					variableInputs.add(recipeItemOptions);
				}
			}
			return Arrays.asList(new CraftingIngredients(fixedInputs, variableInputs));
		}
	}

	protected static class PECustomRecipeMapper implements IRecipeMapper {

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
			return recipe instanceof RecipeShapedKleinStar || recipe instanceof RecipeShapelessHidden;
		}

		@Override
		public Iterable<CraftingIngredients> getIngredientsFor(IRecipe recipe) {
			Iterable recipeItems = null;
			if (recipe instanceof RecipeShapedKleinStar) {
				recipeItems = Arrays.asList(((RecipeShapedKleinStar) recipe).recipeItems);
			} else if (recipe instanceof RecipeShapelessHidden) {
				recipeItems = ((RecipeShapelessHidden) recipe).getInput();
			}
			List<ItemStack> inputs = new LinkedList<>();
			for (Object o : recipeItems) {
				if (o == null) continue;
				if (o instanceof ItemStack) {
					ItemStack recipeItem = (ItemStack) o;
					inputs.add(recipeItem);
				} else {
					PELogger.logWarn("Illegal Ingredient in Crafting Recipe: " + o.toString());
				}
			}
			return Arrays.asList(new CraftingIngredients(inputs, new LinkedList()));
		}

	}
}
