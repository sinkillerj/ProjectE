package moze_intel.projecte.emc;

import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;

public final class RecipeMapper
{
	private static LinkedHashMap<SimpleStack, LinkedList<RecipeInput>> recipes = new LinkedHashMap<SimpleStack, LinkedList<RecipeInput>>();
	
	public static void map()
	{
		Iterator<IRecipe> iter = CraftingManager.getInstance().getRecipeList().iterator();

		while (iter.hasNext())
		{
			IRecipe recipe = iter.next();
			ItemStack output = recipe.getRecipeOutput();

			if (output == null || output.getItem() == null || output.stackSize < 1)
			{
				continue;
			}

			SimpleStack outStack = new SimpleStack(output);

			if (!outStack.isValid())
			{
				continue;
			}

			List<Object> inputs = new ArrayList<Object>();

			if (recipe instanceof ShapedRecipes)
			{
				inputs.addAll(Arrays.asList(((ShapedRecipes) recipe).recipeItems));
			}
			else if (recipe instanceof ShapelessRecipes)
			{
				inputs.addAll(((ShapelessRecipes) recipe).recipeItems);
			}
			else if (recipe instanceof ShapedOreRecipe)
			{
				for (Object obj : ((ShapedOreRecipe) recipe).getInput())
				{
					if (obj == null)
					{
						continue;
					}

					if (obj instanceof ItemStack)
					{
						inputs.add(obj);
					}
					else if (obj instanceof ArrayList)
					{
						inputs.add(obj);
					}
					else
					{
						PELogger.logInfo("Uknown object for shaped recipe: " + obj);
					}
				}
			}
			else if (recipe instanceof ShapelessOreRecipe)
			{
				for (Object obj : ((ShapelessOreRecipe) recipe).getInput())
				{
					if (obj == null)
					{
						continue;
					}

					if (obj instanceof ItemStack)
					{
						inputs.add(obj);
					}
					else if (obj instanceof ArrayList)
					{
						inputs.add(obj);
					}
					else
					{
						PELogger.logInfo("Uknown object for shapeless recipe: " + obj);
					}
				}
			}

			try
			{
				RecipeInput rInput = new RecipeInput();

				for (Object obj : inputs)
				{
					if (obj == null)
					{
						continue;
					}

					if (obj instanceof ItemStack)
					{
						try {
							ItemStack stack = ((ItemStack) obj).copy();

							if (stack == null)
							{
								continue;
							}

							if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
							{
								stack.setItemDamage(0);
							}

							if (stack.getItem().doesContainerItemLeaveCraftingGrid(stack))
							{
								rInput.addToInputs(stack);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
					else
					{
						ArrayList<ItemStack> listCopy = new ArrayList<ItemStack>();

						for (ItemStack stack : (ArrayList<ItemStack>) obj)
						{
							if (stack == null)
							{
								continue;
							}

							if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
							{
								ItemStack copy = stack.copy();
								copy.setItemDamage(0);
								listCopy.add(copy);
							}
							else
							{
								listCopy.add(stack);
							}
						}

						rInput.addToInput(listCopy);
					}
				}

				LinkedList<RecipeInput> currentInputs;

				if (recipes.containsKey(outStack))
				{
					currentInputs = recipes.get(outStack);
				}
				else
				{
					currentInputs = new LinkedList<RecipeInput>();
				}

				currentInputs.add(rInput);

				recipes.put(outStack, currentInputs);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static Set<Map.Entry<SimpleStack, LinkedList<RecipeInput>>> getEntrySet()
	{
		return recipes.entrySet();
	}
}
