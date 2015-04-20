package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.SimpleStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
		return false;
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config)
	{
		try {
			int recipeCount = 0;
			System.out.println("NEI has " + GuiCraftingRecipe.craftinghandlers.size() + " CraftingHandlers");
			for (IRecipeHandler recipeHandler: GuiCraftingRecipe.craftinghandlers) {
				System.out.println(recipeHandler);
				if (recipeHandler != null) {
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
							List<NormalizedSimpleStack> ingredientsNSS = Lists.newLinkedList();
							for (PositionedStack ingredient: ingredients)
							{
								NormalizedSimpleStack ingredientNSS;
								if (ingredient.items.length == 1) {
									ingredientNSS = NormalizedSimpleStack.getNormalizedSimpleStackFor(ingredient.items[0]);
								} else {
									ingredientNSS = NormalizedSimpleStack.createGroup(Arrays.asList(ingredient.items));
									for (ItemStack itemStack: ingredient.items) {
										mapper.addConversion(1, ingredientNSS, Arrays.asList(NormalizedSimpleStack.getNormalizedSimpleStackFor(itemStack)));
									}
								}
								ingredientsNSS.add(ingredientNSS);
							}
							mapper.addConversion(outStack.stackSize, NormalizedSimpleStack.getNormalizedSimpleStackFor(outStack), ingredientsNSS);
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
