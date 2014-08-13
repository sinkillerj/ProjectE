package moze_intel.EMC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import moze_intel.MozeCore;
import moze_intel.gameObjs.ObjHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeMapper 
{
	private static LinkedHashMap<IStack, LinkedList<RecipeInput>> recipes = new LinkedHashMap();	
	
	public static void map()
	{
		Iterator<IRecipe> iter = CraftingManager.getInstance().getRecipeList().iterator();
		
		while (iter.hasNext())
		{
			IRecipe recipe = iter.next();
			ItemStack output = recipe.getRecipeOutput();
			
			if (output == null)
			{
				continue;
			}
			
			if (output.stackSize < 1)
			{
				MozeCore.logger.logFatal("Recipe with stack-size of 0: "+output);
				continue;
			}
			
			if (output.getItem() == null)
			{
				MozeCore.logger.logFatal("ItemStack with null item: "+output);
				continue;
			}
			
			IStack outStack = new IStack(output);
			List<ItemStack> inputs = new ArrayList();
			
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
					else if (obj instanceof ItemStack)
					{
						inputs.add((ItemStack) obj);
					}
					else if (obj instanceof ArrayList)
					{
						ArrayList l = (ArrayList) obj;
						
						if (l.isEmpty())
						{
							continue;
						}
						
						inputs.add((ItemStack) l.get(0));
					}
					else
					{
						MozeCore.logger.logInfo("Uknown object for shaped recipe: "+obj);
					}
				}
			}
			else if (recipe instanceof ShapelessOreRecipe)
			{
				ArrayList<Object> l = ((ShapelessOreRecipe) recipe).getInput();
				
				for (Object obj : l)
				{
					if (obj == null)
					{
						continue;
					}
					else if (obj instanceof ItemStack)
					{
						inputs.add((ItemStack) obj);
					}
					else if (obj instanceof ArrayList)
					{
						ArrayList l2 = (ArrayList) obj;
						
						if (l2.isEmpty())
						{
							continue;
						}
						
						inputs.add((ItemStack) l2.get(0));
					}
					else
					{
						MozeCore.logger.logInfo("Uknown object for shapeless recipe: "+obj);
					}
				}
			}
			
			try
			{
				RecipeInput rInput = new RecipeInput();
				
				for (ItemStack stack : inputs)
				{
					if (stack != null && stack.getItem().doesContainerItemLeaveCraftingGrid(stack))
					{
						rInput.addToInputs(stack);
					}
				}
				
				LinkedList<RecipeInput> currentInputs;
				
				if (recipes.containsKey(outStack))
				{
					currentInputs = recipes.get(outStack);
				}
				else
				{
					currentInputs = new LinkedList();
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
	
	public static Set<Entry<IStack, LinkedList<RecipeInput>>> getEntrySet()
	{
		return recipes.entrySet();
	}
}
