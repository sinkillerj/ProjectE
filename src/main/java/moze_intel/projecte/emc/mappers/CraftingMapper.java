package moze_intel.projecte.emc.mappers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.json.NSSFake;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapedKleinStar;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CraftingMapper implements IEMCMapper<NormalizedSimpleStack, Integer> {

	private final List<IRecipeMapper> recipeMappers = Arrays.asList(new VanillaRecipeMapper(), new VanillaOreRecipeMapper(), new PECustomRecipeMapper());
	private final Set<Class> canNotMap = Sets.newHashSet();
	private final Map<Class, Integer> recipeCount = Maps.newHashMap();

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, final Configuration config) {
		recipeCount.clear();
		canNotMap.clear();
		recipeloop: for (IRecipe recipe : CraftingManager.field_193380_a) {
			boolean handled = false;
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput == null) continue;
			NormalizedSimpleStack recipeOutputNorm = NSSItem.create(recipeOutput);
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
								if (stack.isEmpty()) continue;
								if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
									//Don't check for doesContainerItemLeaveCraftingGrid for WILDCARD-ItemStacks
									ingredientMap.addIngredient(NSSItem.create(stack), 1);
								} else {
									//stack does not have a wildcard damage value
									try
									{
										//if (stack.getItem().doesContainerItemLeaveCraftingGrid(stack))
										{
											if (stack.getItem().hasContainerItem(stack))
											{
												ingredientMap.addIngredient(NSSItem.create(stack.getItem().getContainerItem(stack)), -1);
											}
											ingredientMap.addIngredient(NSSItem.create(stack), 1);
										}
										//else if (config.getBoolean("emcDependencyForUnconsumedItems", "", true, "If this option is enabled items that are made by crafting, with unconsumed ingredients, should only get an emc value, if the unconsumed item also has a value. (Examples: Extra Utilities Sigil, Cutting Board, Mixer, Juicer...)"))
										//{
											//Container Item does not leave the crafting grid: we add an EMC dependency anyway.
											//ingredientMap.addIngredient(NormalizedSimpleStack.getFor(stack), 0);
										//} TODO 1.8 method doesContainerItemLeave... no longer exists
									} catch (Exception e) {
										PECore.LOGGER.fatal("Exception in CraftingMapper when parsing Recipe Ingredients: RecipeType: {}, Ingredient: {}", recipe.getClass().getName(), stack.toString());
										e.printStackTrace();
										continue recipeloop;
									}
								}
							}
							for (Iterable<ItemStack> multiIngredient : variation.multiIngredients) {
								NormalizedSimpleStack normalizedSimpleStack = NSSFake.create(multiIngredient.toString());
								ingredientMap.addIngredient(normalizedSimpleStack, 1);
								for (ItemStack stack : multiIngredient) {
									if (stack.isEmpty()) continue;
									//if (stack.getItem().doesContainerItemLeaveCraftingGrid(stack)) {
										IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
										if (stack.getItem().hasContainerItem(stack)) {
											groupIngredientMap.addIngredient(NSSItem.create(stack.getItem().getContainerItem(stack)), -1);
										}
										groupIngredientMap.addIngredient(NSSItem.create(stack), 1);
										mapper.addConversion(1, normalizedSimpleStack, groupIngredientMap.getMap());
									//} TODO 1.8 method doesContainerItemLeave... no longer exists
								}
							}
							if (recipeOutput.getCount() > 0) {
								mapper.addConversion(recipeOutput.getCount(), recipeOutputNorm, ingredientMap.getMap());
							} else {
								PECore.LOGGER.warn("Ignoring Recipe because outnumber <= 0: {} -> {}", ingredientMap.getMap().toString(), recipeOutput);
							}
						}
					} else {
						PECore.LOGGER.warn("RecipeMapper {} failed to map Recipe {}", recipeMapper, recipe);
					}
					break;
				}
			}
			if (!handled) {
				if (!canNotMap.contains(recipe.getClass())) {
					canNotMap.add(recipe.getClass());
					PECore.LOGGER.warn("Can not map Crafting Recipes with Type: {}", recipe.getClass().getName());
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

		PECore.LOGGER.info("CraftingMapper Statistics:");
		for (Map.Entry<Class, Integer> entry: recipeCount.entrySet()) {
			PECore.LOGGER.info("Found {} Recipes of Type {}", entry.getValue(), entry.getKey());
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

		Iterable<CraftingIngredients> getIngredientsFor(IRecipe recipe);
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
					PECore.LOGGER.warn("Illegal Ingredient in Crafting Recipe: {}", o);
				}
			}
			return Collections.singletonList(new CraftingIngredients(inputs, new LinkedList<>()));
		}

	}

	private static class VanillaOreRecipeMapper implements IRecipeMapper {

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
			Iterable<Ingredient> recipeItems = recipe.func_192400_c();
			ArrayList<Iterable<ItemStack>> variableInputs = Lists.newArrayList();
			ArrayList<ItemStack> fixedInputs = Lists.newArrayList();
			for (Ingredient recipeItem : recipeItems) {
				List<ItemStack> recipeItemOptions = new LinkedList<>();
				ItemStack[] recipeItemCollection = recipeItem.func_193365_a();
				if (recipeItemCollection.length == 1) {
					fixedInputs.add(recipeItemCollection[0].copy());
					continue;
				}
				for (ItemStack option : recipeItemCollection) {
					recipeItemOptions.add(option.copy());
				}
				variableInputs.add(recipeItemOptions);
			}
			return Collections.singletonList(new CraftingIngredients(fixedInputs, variableInputs));
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
					PECore.LOGGER.warn("Illegal Ingredient in Crafting Recipe: {}", o);
				}
			}
			return Collections.singletonList(new CraftingIngredients(inputs, new LinkedList<>()));
		}

	}
}
