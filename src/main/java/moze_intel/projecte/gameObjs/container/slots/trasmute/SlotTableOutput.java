package moze_intel.projecte.gameObjs.container.slots.trasmute;

import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
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
	public ItemStack decrStackSize(int slot)
	{
		ItemStack stack = getStack().copy();
		stack.stackSize = slot;
		tile.removeItemRelativeEmcWithPKT(stack);
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
	public boolean canTakeStack(EntityPlayer player)
	{
		return true;
	}
}
