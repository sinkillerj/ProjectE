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
	protected ItemStack output = null;
	protected final ArrayList<Object> input = new ArrayList<>();

	public RecipeShapelessHidden(Block result, Object... recipe)
	{
		this(new ItemStack(result), recipe);
	}

	public RecipeShapelessHidden(Item result, Object... recipe)
	{
		this(new ItemStack(result), recipe);
	}

	public RecipeShapelessHidden(ItemStack result, Object... recipe)
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

	@SuppressWarnings("unchecked")
	RecipeShapelessHidden(ShapelessRecipes recipe, Map<ItemStack, String> replacements)
	{
		output = recipe.getRecipeOutput();

		for(ItemStack ingred : recipe.recipeItems)
		{
			Object finalObj = ingred;
			for(Entry<ItemStack, String> replace : replacements.entrySet())
			{
				if(OreDictionary.itemMatches(replace.getKey(), ingred, false))
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
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1){ return output.copy(); }

	/**
	 * Used to check if a recipe matches current crafting inventory
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
	{
		ArrayList<Object> required = new ArrayList<>(input);

		double storedEMC = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null && stack.getItem() == ObjHandler.kleinStars)
			{
				storedEMC += KleinStar.getEmc(stack);
			}
		}

		if (output.getItem() == ObjHandler.kleinStars)
		{
			if (!output.hasTagCompound())
			{
				output.setTagCompound(new NBTTagCompound());
			}
			KleinStar.setEmc(output, storedEMC);
		}
		
		for (int x = 0; x < inv.getSizeInventory(); x++)
		{
			ItemStack slot = inv.getStackInSlot(x);

			if (slot != null)
			{
				boolean inRecipe = false;

				for (Object aRequired : required) {
					boolean match = false;

					if (aRequired instanceof ItemStack) {
						match = OreDictionary.itemMatches((ItemStack) aRequired, slot, false);
					} else if (aRequired instanceof List) {
						Iterator<ItemStack> itr = ((List<ItemStack>) aRequired).iterator();
						while (itr.hasNext() && !match) {
							match = OreDictionary.itemMatches(itr.next(), slot, false);
						}
					}

					if (match) {
						inRecipe = true;
						required.remove(aRequired);
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
	public ArrayList<Object> getInput()
	{
		return this.input;
	}

	@Nonnull
	@Override
	public ItemStack[] getRemainingItems(@Nonnull InventoryCrafting inv) //getRecipeLeftovers
	{
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}}