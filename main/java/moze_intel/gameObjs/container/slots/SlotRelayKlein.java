package moze_intel.gameObjs.container.slots;

import moze_intel.gameObjs.ObjHandler;
import moze_intel.utils.Constants;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotRelayKlein extends Slot
{
	public SlotRelayKlein(IInventory inventory, int slotIndex, int xPos, int yPos) 
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
		
		return stack.getItem() == ObjHandler.kleinStars;
    }
}
