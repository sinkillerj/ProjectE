package moze_intel.projecte.gameObjs.container.slots.condenser;

import moze_intel.projecte.gameObjs.tiles.CondenserMK2Tile;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCondenserMK2Output extends Slot
{
	public SlotCondenserMK2Output(CondenserMK2Tile inventory, int slotIndex, int xPos, int yPos)
	{
		super(inventory, slotIndex, xPos, yPos);
	}

	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}
}
