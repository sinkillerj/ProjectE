package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.KleinStar;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeKleinStar implements IRecipe
{
	private ItemStack output;
	private ItemStack input;
	private int inputDamage;

	public RecipeKleinStar(ItemStack output, ItemStack input)
	{
		this.output = output;
		this.input = input;
		this.inputDamage = input.getItemDamage();
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) 
	{
		double storedEMC = 0;
		int starCount = 0;
		
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack isInSlot = inv.getStackInSlot(i);
			
			if (isInSlot == null)
			{
				continue;
			}
			
			if (isInSlot.getItem() != ObjHandler.kleinStars)
			{
				return false;
			}
			
			if (inputDamage >= 5)
			{
				return false;
			}
			
			else
			{
				if (isInSlot.getItemDamage() != inputDamage)
				{
					return false;
				}
			}
			
			starCount++;
			
			if (starCount > 4)
			{
				return false;
			}
			
			storedEMC += KleinStar.getEmc(isInSlot);
		}
		
		if (starCount == 4)
		{
			output.setTagCompound(new NBTTagCompound());
			KleinStar.setEmc(output, storedEMC);
			
			return true;
		}
		
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) 
	{
		return output.copy();
	}

	@Override
	public int getRecipeSize() 
	{
		return 4;
	}

	@Override
	public ItemStack getRecipeOutput() 
	{
		return output;
	}
}
