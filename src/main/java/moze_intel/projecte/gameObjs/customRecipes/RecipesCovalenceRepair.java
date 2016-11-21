package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RecipesCovalenceRepair implements IRecipe
{
	private ItemStack output;

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
	{
		List<ItemStack> dust = new ArrayList<>();
		ItemStack tool = null;
		boolean foundItem = false;
		int dustEmc = 0;
		int emcPerDurability = 0;
		
		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack input = inv.getStackInSlot(i);
			
			if (input == null)
			{
				continue;
			}
			
			if (ItemHelper.isItemRepairable(input))
			{
				if (!foundItem)
				{
					tool = input;
					foundItem = true;
					emcPerDurability = EMCHelper.getEMCPerDurability(tool);
				}
				else
				{
					return false;
				}
			}
			else if (input.getItem() == ObjHandler.covalence)
			{
				dust.add(input);
			}
		}
		
		if (tool == null || !foundItem || dust.size() == 0)
		{
			return false;
		}

		for (ItemStack stack : dust) {
			dustEmc += EMCHelper.getEmcValue(stack);
		}
		if(dustEmc < emcPerDurability){
			return false;
		}
		
		output = tool.copy();
		output.setItemDamage(Math.max(tool.getItemDamage() - dustEmc / emcPerDurability, 0));
		return true;
	}
	
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting var1)
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
