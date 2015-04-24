package moze_intel.projecte.gameObjs.container.slots.condenser;

import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCondenserInput extends Slot
{
	public SlotCondenserInput(CondenserTile inventory, int slotIndex, int xPos, int yPos)
	{
		super(inventory, slotIndex, xPos, yPos);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return EMCHelper.doesItemHaveEmc(stack);
	}
}
