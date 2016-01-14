package moze_intel.projecte.gameObjs.container.slots.transmutation;

import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotOutput extends Slot
{
	private TransmutationInventory inv;
	
	public SlotOutput(TransmutationInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
		this.inv = inv;
	}
	
	@Override
	public ItemStack decrStackSize(int amount)
	{
		ItemStack stack = getStack().copy();
		stack.stackSize = amount;
		int emcValue = amount * EMCHelper.getEmcValue(stack);
		if (emcValue > inv.emc) {
			//Requesting more emc than available
			//Can not return `null` here or NPE in Container! Container expects stacksize=0-Itemstack for 'nothing'
			stack.stackSize = 0;
			return stack;
		}
		inv.removeEmc(emcValue);
		inv.checkForUpdates();
		
		return stack;
	}
	
	@Override
	public void putStack(ItemStack stack) {}
	
	@Override
	public boolean isItemValid(ItemStack par1ItemStack)
	{
		return false;
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player)
	{
		if (getHasStack()) {
			return EMCHelper.getEmcValue(getStack()) <= inv.emc;
		}
		return true;
	}
}
