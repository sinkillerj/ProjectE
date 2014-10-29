package moze_intel.projecte.gameObjs.container.slots.condenser;

import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.utils.Utils;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCondenserLock extends Slot 
{
	private CondenserTile tile;
	
	public SlotCondenserLock(CondenserTile inventory, int slotIndex, int xPos, int yPos) 
	{
		super(inventory, slotIndex, xPos, yPos);
		tile = inventory;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
    {
        return Utils.doesItemHaveEmc(stack);
    }
	
	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
