package moze_intel.projecte.gameObjs.container.slots.transmutation;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotInput extends Slot
{
	private TransmutationInventory inv;
	
	public SlotInput(TransmutationInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
		this.inv = inv;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return !this.getHasStack() && EMCHelper.doesItemHaveEmc(stack);
	}
	
	@Override
	public void putStack(ItemStack stack)
	{
		if (stack == null)
		{
			return;
		}
		
		super.putStack(stack);
		
		if (stack.getItem() instanceof IItemEmc)
		{
			IItemEmc itemEmc = ((IItemEmc) stack.getItem());
			double remainingEmc = itemEmc.getMaximumEmc(stack) - (int) Math.ceil(itemEmc.getStoredEmc(stack));
			
			if (inv.emc >= remainingEmc)
			{
				itemEmc.addEmc(stack, remainingEmc);
				inv.removeEmc(remainingEmc);
			}
			else
			{
				itemEmc.addEmc(stack, inv.emc);
				inv.emc = 0;
			}
		}
		
		if (stack.getItem() != ObjHandler.tome)
		{
			inv.handleKnowledge(stack.copy());
		}
		else
		{
			inv.updateOutputs();
		}
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
