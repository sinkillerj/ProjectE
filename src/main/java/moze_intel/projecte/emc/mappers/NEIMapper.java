package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.SimpleStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NEIMapper implements IEMCMapper<NormalizedSimpleStack, Integer>
{
	@Override
	public String getName()
	{
		return "NEIMapper";
	}

	@Override
	public String getDescription()
	{
		return "Reads Recipes from the GUIs that are displayed by NEI. ONLY WORKS IN SINGLEPLAYER!";
	}

	@Override
	public boolean isAvailable()
	{
		return getCraftingHandlersFromNEI() != null;
	}

	protected Collection getCraftingHandlersFromNEI() {
		try
		{
			Class clazz = Class.forName("codechicken.nei.recipe.GuiCraftingRecipe");
			Field f = clazz.getDeclaredField("craftinghandlers");
			Object craftinghandlers = f.get(null);
			if (craftinghandlers instanceof Collection) {
				return (Collection)craftinghandlers;
			} else {
				return null;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config)
	{
		try {
			int recipeCount = 0;
			Collection craftinghandlers = getCraftingHandlersFromNEI();
			if (craftinghandlers == null) return;
			System.out.println("NEI has " + craftinghandlers.size() + " CraftingHandlers");
			for (Object o: craftinghandlers) {
				if (o != null && o instanceof IRecipeHandler) {
					IRecipeHandler recipeHandler = (IRecipeHandler)o;
					System.out.println(recipeHandler);
					if (!(recipeHandler instanceof TemplateRecipeHandler)) {
						System.out.println("Not TemplateRecipeHandler - ignoring");
						continue;
					}
					try {
						TemplateRecipeHandler trh = (TemplateRecipeHandler)recipeHandler;
						trh.loadCraftingRecipes(trh.getOverlayIdentifier());
						System.out.println(recipeHandler.numRecipes());
						for (int recipeNumber = 0; recipeNumber < recipeHandler.numRecipes(); recipeNumber++) {
							List<PositionedStack> ingredients = recipeHandler.getIngredientStacks(recipeNumber);
							ItemStack outStack = recipeHandler.getResultStack(recipeNumber).item;
							IngredientMap<NormalizedSimpleStack> ingredientsNSSMap = new IngredientMap<NormalizedSimpleStack>();
							for (PositionedStack ingredient: ingredients)
							{
								NormalizedSimpleStack ingredientNSS;
								if (ingredient.items.length == 1) {
									ingredientNSS = NormalizedSimpleStack.getNormalizedSimpleStackFor(ingredient.items[0]);
									ingredientsNSSMap.addIngredient(ingredientNSS, ingredient.items[0].stackSize);
								} else {
									ingredientNSS = NormalizedSimpleStack.createGroup(Arrays.asList(ingredient.items));
									Map<NormalizedSimpleStack, Integer> groupCountMap = Maps.newHashMap();
									for (ItemStack itemStack: ingredient.items) {
										groupCountMap.put(NormalizedSimpleStack.getNormalizedSimpleStackFor(itemStack), itemStack.stackSize);
										mapper.addConversionMultiple(1, ingredientNSS, groupCountMap);
										groupCountMap.clear();
									}
									ingredientsNSSMap.addIngredient(ingredientNSS, 1);
								}

							}
							mapper.addConversionMultiple(outStack.stackSize, NormalizedSimpleStack.getNormalizedSimpleStackFor(outStack), ingredientsNSSMap.getMap());
						}
					} catch (Exception e) {
						System.out.println("Could not get Recipes from IRecipeHandler" + recipeHandler.toString());
					}
				}
			}
			System.out.println("Loaded " + recipeCount + " Recipes from NEI");
		} catch (Exception e) {
			System.out.println("Could not load Recipes from NEI");
		}
	}
}
