package moze_intel.gameObjs.container.slots;

import moze_intel.gameObjs.tiles.CondenserTile;
import moze_intel.utils.Utils;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCondenserInput extends Slot
{
	private CondenserTile tile;
	
	public SlotCondenserInput(CondenserTile inventory, int slotIndex, int xPos, int yPos) 
	{
		super(inventory, slotIndex, xPos, yPos);
		tile = inventory;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack)
    {
        return Utils.doesItemHaveEmc(stack);
    }
}
