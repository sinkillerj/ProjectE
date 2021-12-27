package moze_intel.projecte.gameObjs.container.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

/**
 * @implNote From Mekanism
 */
public class InsertableSlot extends Slot implements IInsertableSlot {

    public InsertableSlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(getMaxStackSize(), stack.getMaxStackSize());
    }
}