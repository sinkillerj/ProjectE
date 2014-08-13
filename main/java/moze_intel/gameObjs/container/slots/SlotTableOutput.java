package moze_intel.gameObjs.container.slots;

import moze_intel.gameObjs.tiles.TransmuteTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTableOutput extends Slot
{
	private TransmuteTile tile;
	
	public SlotTableOutput(TransmuteTile tile, int par2, int par3, int par4) 
	{
		super(tile, par2, par3, par4);
		this.tile = tile;
	}
	
	@Override
	public ItemStack decrStackSize(int par1)
	{
		for (int i = 0; i <= 7; i++)
		{
			tile.setInventorySlotContents(i, null);
		}
		
		ItemStack stack = getStack().copy();
		stack.stackSize = par1;
		tile.RemoveItemRelativeEmc(stack);
		tile.checkForUpdates();
		
		return stack;
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		return;
	}
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
    {
        return false;
    }
	
	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer)
	{
		return true;
	}
}
