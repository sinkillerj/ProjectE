package moze_intel.projecte.gameObjs.container.slots.collector;

import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import net.minecraftforge.items.IItemHandler;

public class SlotCollectorLock extends ValidatedSlot
{
	public SlotCollectorLock(IItemHandler inventory, int slotIndex, int xPos, int yPos)
	{
		super(inventory, slotIndex, xPos, yPos, SlotPredicates.COLLECTOR_LOCK);
	}

	@Override
	public int getSlotStackLimit()
	{
		return 1;
	}
}
