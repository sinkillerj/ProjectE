package moze_intel.projecte.gameObjs.tiles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moze_intel.projecte.api.item.IItemEmc;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class DMFurnaceTile extends RMFurnaceTile implements IInventory, ISidedInventory
{
	public DMFurnaceTile()
	{
		this.inventory = new ItemStack[19];
		this.ticksBeforeSmelt = 10;
		this.efficiencyBonus = 3;
		this.outputSlot = 10;
		this.inputStorage = new int[] {2, 9};
		this.outputStorage = new int[] {11, 18};
	}
	
	@Override
	public int getSizeInventory() 
	{
		return 19;
	}
	
	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int value)
	{
		return furnaceCookTime * value / ticksBeforeSmelt;
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) 
	{
		if (stack == null)
		{
			return false;
		}
		
		if (slot == 0)
		{
			return TileEntityFurnace.isItemFuel(stack) || stack.getItem() instanceof IItemEmc;
		}
		else if (slot >= 1 && slot <= 9)
		{
			return FurnaceRecipes.smelting().getSmeltingResult(stack) != null;
		}
		
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		switch(side)
		{
			case 0: return new int[] {11, 12, 13, 14, 15, 16, 17, 18}; // Outputs accessible from bottom
			case 1: return new int[] {2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15, 16, 17, 18}; // Inputs accessible from top
			case 2: // Fall through
			case 3:
			case 4:
			case 5: return new int[] {0, 11, 12, 13, 14, 15, 16, 17, 18}; // Fuel and output accessible from all sides
			default: return new int[] {};
		}
	}

	@Override
	public String getInventoryName()
	{
		return "pe.dmfurnace.shortname";
	}
}