package moze_intel.projecte.gameObjs.container.slots.transmuteportable;

import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotTabletOutput extends Slot
{
	private TransmuteTabletInventory table;
	
	public SlotTabletOutput(TransmuteTabletInventory table, int par2, int par3, int par4) 
	{
		super(table, par2, par3, par4);
		this.table = table;
	}
	
	@Override
	public ItemStack decrStackSize(int slot)
	{
		ItemStack stack = getStack().copy();
		stack.stackSize = slot;
		table.removeEmc(Utils.getEmcValue(stack));
		table.checkForUpdates();
		
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
