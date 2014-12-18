package sinkillerj.projecte.gameObjs.container.slots.condenser;

import sinkillerj.projecte.gameObjs.tiles.CondenserTile;
import sinkillerj.projecte.utils.Utils;
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
        return Utils.doesItemHaveEmc(stack);
    }
}
