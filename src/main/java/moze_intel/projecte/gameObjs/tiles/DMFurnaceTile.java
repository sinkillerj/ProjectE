package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DMFurnaceTile extends RMFurnaceTile
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
			return TileEntityFurnace.isItemFuel(stack) || stack.getItem() == ObjHandler.kleinStars;
		}
		else if (slot >= 1 && slot <= 9)
		{
			return FurnaceRecipes.instance().getSmeltingResult(stack) != null;
		}
		
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side)
	{
		switch(side)
		{
			case DOWN: return new int[] {11, 12, 13, 14, 15, 16, 17, 18}; // Outputs accessible from bottom
			case UP: return new int[] {2, 3, 4, 5, 6, 7, 8, 9, 11, 12, 13, 14, 15, 16, 17, 18}; // Inputs accessible from top
			case NORTH: // Fall through
			case SOUTH:
			case WEST:
			case EAST: return new int[] {0, 11, 12, 13, 14, 15, 16, 17, 18}; // Fuel and output accessible from all sides
			default: return new int[] {};
		}
	}

	@Override
	public String getCommandSenderName()
	{
		return "pe.dmfurnace.shortname";
	}
}