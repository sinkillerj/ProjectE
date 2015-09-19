package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeAlchemyBag implements IRecipe
{
	private ItemStack output;
	private ItemStack inputBag;
	private ItemStack inputDye;

	public RecipeAlchemyBag(ItemStack output, ItemStack inputBag, ItemStack inputDye)
	{
		this.output = output;
		this.inputBag = inputBag;
		this.inputDye = inputDye;

		if (inputBag.hasTagCompound())
		{
			output.stackTagCompound = inputBag.stackTagCompound;
		}
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world)
	{
		boolean foundBag = false;
		boolean foundDye = false;

		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack isInSlot = inv.getStackInSlot(i);

			if (isInSlot == null)
			{
				continue;
			}

			if (isInSlot.getItem() == ObjHandler.alchBag)
			{
				if (foundBag || isInSlot.getItemDamage() != inputBag.getItemDamage())
				{
					return false;
				}

				foundBag = true;
			}
			if (isInSlot.getItem() == Items.dye)
			{
				if (foundDye || isInSlot.getItemDamage() != inputDye.getItemDamage())
				{
					return false;
				}

				foundDye = true;
			}
		}

		if (foundBag && foundDye)
		{
			if (inputBag.getItemDamage() != 0 && inputDye.getItemDamage() == 15)
			{
				return true;
			} else if (inputBag.getItemDamage() == 0 && inputDye.getItemDamage() != 15)
			{
				return true;
			} else
			{
				return false;
			}
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv)
	{
		return output.copy();
	}

	@Override
	public int getRecipeSize()
	{
		return 10;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return output;
	}

	public ItemStack getRecipeInputBag()
	{
		return inputBag;
	}

	public ItemStack getRecipeInputDye()
	{
		return inputDye;
	}
}
