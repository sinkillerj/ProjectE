package moze_intel.projecte.gameObjs.container.slots.mercurial;

import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import net.minecraftforge.items.IItemHandler;

public class SlotMercurialTarget extends ValidatedSlot
{
	public SlotMercurialTarget(IItemHandler par1iInventory, int par2, int par3, int par4)
	{
		super(par1iInventory, par2, par3, par4, SlotPredicates.MERCURIAL_TARGET);
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
