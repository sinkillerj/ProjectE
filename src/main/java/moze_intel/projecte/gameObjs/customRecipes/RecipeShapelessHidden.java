package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.KleinStar;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

// This is literally just ShapelessOreRecipe, unchanged. NEI doesn't auto-pick up shapeless recipes registered this way,
// thus hiding those recipes from the Shapeless Recipes list.
public class RecipeShapelessHidden implements IRecipe
{
	protected ItemStack output = ItemStack.EMPTY;
	protected NonNullList<Object> input = NonNullList.create();

	public RecipeShapelessHidden(Block result, Object... recipe){ this(new ItemStack(result), recipe); }
	public RecipeShapelessHidden(Item  result, Object... recipe){ this(new ItemStack(result), recipe); }

	public RecipeShapelessHidden(@Nonnull ItemStack result, Object... recipe)
	{
		output = result.copy();
		for (Object in : recipe)
		{
			if (in instanceof ItemStack)
			{
				input.add(((ItemStack)in).copy());
			}
			else if (in instanceof Item)
			{
				input.add(new ItemStack((Item)in));
			}
			else if (in instanceof Block)
			{
				input.add(new ItemStack((Block)in));
			}
			else if (in instanceof String)
			{
				input.add(OreDictionary.getOres((String)in));
			}
			else
			{
				String ret = "Invalid shapeless ore recipe: ";
				for (Object tmp :  recipe)
				{
					ret += tmp + ", ";
				}
				ret += output;
				throw new RuntimeException(ret);
			}
		}
	}

	RecipeShapelessHidden(ShapelessRecipes recipe, Map<ItemStack, String> replacements)
	{
		output = recipe.getRecipeOutput();

		for(ItemStack ingredient : recipe.recipeItems)
		{
			Object finalObj = ingredient;
			for(Entry<ItemStack, String> replace : replacements.entrySet())
			{
				if(OreDictionary.itemMatches(replace.getKey(), ingredient, false))
				{
					finalObj = OreDictionary.getOres(replace.getValue());
					break;
				}
			}
			input.add(finalObj);
		}
	}

	/**
	 * Returns the size of the recipe area
	 */
	@Override
	public int getRecipeSize(){ return input.size(); }

	@Override
	public ItemStack getRecipeOutput(){ return output; }

	/**
	 * Returns an Item that is the result of this recipe
	 */
	@Override
	public ItemStack getCraftingResult(InventoryCrafting var1){ return output.copy(); }

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean matches(InventoryCrafting var1, World world)
	{
		NonNullList<Object> required = NonNullList.create();
		required.addAll(input);

		for (int x = 0; x < var1.getSizeInventory(); x++)
		{
			ItemStack slot = var1.getStackInSlot(x);

			if (!slot.isEmpty())
			{
				boolean inRecipe = false;
				Iterator<Object> req = required.iterator();

				while (req.hasNext())
				{
					boolean match = false;

					Object next = req.next();

					if (next instanceof ItemStack)
					{
						match = OreDictionary.itemMatches((ItemStack)next, slot, false);
					}
					else if (next instanceof List)
					{
						Iterator<ItemStack> itr = ((List<ItemStack>)next).iterator();
						while (itr.hasNext() && !match)
						{
							match = OreDictionary.itemMatches(itr.next(), slot, false);
						}
					}

					if (match)
					{
						inRecipe = true;
						required.remove(next);
						break;
					}
				}

				if (!inRecipe)
				{
					return false;
				}
			}
		}

		return required.isEmpty();
	}

	/**
	 * Returns the input for this recipe, any mod accessing this value should never
	 * manipulate the values in this array as it will effect the recipe itself.
	 * @return The recipes input vales.
	 */
	public NonNullList<Object> getInput()
	{
		return this.input;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) //getRecipeLeftovers
	{
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}