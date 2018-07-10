package moze_intel.projecte.gameObjs.container.slots.transmutation;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotInput extends SlotItemHandler
{
	private final TransmutationInventory inv;
	
	public SlotInput(TransmutationInventory inv, int par2, int par3, int par4)
	{
		super(inv, par2, par3, par4);
		this.inv = inv;
	}
	
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack)
	{
		return SlotPredicates.RELAY_INV.test(stack);
	}
	
	@Override
	public void putStack(@Nonnull ItemStack stack)
	{
		if (stack.isEmpty())
		{
			return;
		}
		
		super.putStack(stack);
		
		if (stack.getItem() instanceof IItemEmc)
		{
			IItemEmc itemEmc = ((IItemEmc) stack.getItem());
			long remainingEmc = itemEmc.getMaximumEmc(stack) - (long) Math.ceil(itemEmc.getStoredEmc(stack));
			
			if (inv.provider.getEmc() >= remainingEmc)
			{
				itemEmc.addEmc(stack, remainingEmc);
				inv.removeEmc(remainingEmc);
			}
			else
			{
				itemEmc.addEmc(stack, inv.provider.getEmc());
				inv.removeEmc(inv.provider.getEmc());
			}
		}

		if (EMCHelper.doesItemHaveEmc(stack)) {
			inv.handleKnowledge(stack.copy());
		}
	}
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
