package moze_intel.projecte.gameObjs.container.slots.relay;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotRelayInput extends Slot
{
	public SlotRelayInput(IInventory inventory, int slotIndex, int xPos, int yPos) 
	{
		super(inventory, slotIndex, xPos, yPos);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		if (stack == null)
		{
			return false;
		}
		
		return stack.getItem() instanceof IItemEmc || EMCHelper.doesItemHaveEmc(stack);
	}
}
