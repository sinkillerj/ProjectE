package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;

public class RecipesAlchemyBags implements IRecipe
{
	private ItemStack output;

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
	{
		ItemStack bag = null;
		ItemStack dye = null;
		boolean foundBag = false;
		boolean foundDye = false;
		
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack input = inv.getStackInSlot(i);

			if (input == null)
			{
				continue;
			}

			if (input.getItem() == ObjHandler.alchBag)
			{
				if (foundBag)
				{
					return false;
				}

				bag = input;
				foundBag = true;
			}
			if (input.getItem() == Items.DYE)
			{
				if (foundDye)
				{
					return false;
				}

				dye = input;
				foundDye = true;
			}
		}
		
		if (foundBag && foundDye)
		{
			if (bag.getItemDamage() != 0 && dye.getItemDamage() == 15)
			{
				output = new ItemStack(ObjHandler.alchBag, 1, 0);

				if (bag.hasTagCompound())
				{
					output.setTagCompound(bag.getTagCompound());
				}

				return true;
			}
			else if (bag.getItemDamage() == 0 && dye.getItemDamage() != 15)
			{
				output = new ItemStack(ObjHandler.alchBag, 1, 15 - dye.getItemDamage());

				if (bag.hasTagCompound())
				{
					output.setTagCompound(bag.getTagCompound());
				}

				return true;
			}
			else
			{
				return false;
			}
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv)
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

	@Nonnull
	@Override
	public ItemStack[] getRemainingItems(@Nonnull InventoryCrafting inv)
	{
		return ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}
