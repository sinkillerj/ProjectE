package moze_intel.gameObjs.container.slots;

import moze_intel.EMC.FuelMapper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCollectorLock extends Slot 
{
	public SlotCollectorLock(IInventory inventory, int slotIndex, int xPos, int yPos) 
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
		
        return FuelMapper.isStackFuel(stack);
    }
}
