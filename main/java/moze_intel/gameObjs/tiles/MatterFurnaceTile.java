package moze_intel.gameObjs.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class MatterFurnaceTile extends TileEntityDirection implements IInventory
{

	@Override
	public int getSizeInventory() 
	{
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int var1) 
	{
		return null;
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) 
	{
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) 
	{
		return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) 
	{
		
	}

	@Override
	public String getInventoryName() 
	{
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() 
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) 
	{
		return false;
	}

	@Override
	public void openInventory() 
	{
		
	}

	@Override
	public void closeInventory() 
	{
		
	}

	@Override
	public boolean isItemValidForSlot(int var1, ItemStack var2) 
	{
		return false;
	}
}
