package moze_intel.gameObjs.customRecipes;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.gameObjs.items.KleinStar;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipesKleinStars implements IRecipe
{
	private ItemStack output;
	
	@Override
	public boolean matches(InventoryCrafting inv, World world) 
	{
		double storedEMC = 0;
		int starDamage = -1;
		int starCount = 0;
		
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack input = inv.getStackInSlot(i);
			
			if (input == null)
			{
				continue;
			}
			
			if (input.getItem() != ObjHandler.kleinStars)
			{
				return false;
			}
			
			if (starDamage == -1)
			{
				starDamage = input.getItemDamage();
				
				if (starDamage >= 5)
				{
					return false;
				}
			}
			else
			{
				if (input.getItemDamage() != starDamage)
				{
					return false;
				}
			}
			
			starCount++;
			
			if (starCount > 4)
			{
				return false;
			}
			
			storedEMC += KleinStar.getEmc(input);
		}
		
		if (starCount == 4)
		{
			output = new ItemStack(ObjHandler.kleinStars, 1, ++starDamage);
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
