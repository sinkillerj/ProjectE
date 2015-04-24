package moze_intel.projecte.gameObjs.container.slots.mercurial;

import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotMercurialTarget extends Slot
{
	public SlotMercurialTarget(IInventory par1iInventory, int par2, int par3, int par4) 
	{
		super(par1iInventory, par2, par3, par4);
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		Block block = Block.getBlockFromItem(stack.getItem());
		return block != null && !(block instanceof ITileEntityProvider) && EMCHelper.doesItemHaveEmc(stack);
	}
}
