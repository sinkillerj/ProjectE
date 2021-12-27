package moze_intel.projecte.gameObjs.container.slots;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class InventoryContainerSlot extends SlotItemHandler implements IInsertableSlot {

    public InventoryContainerSlot(IItemHandler itemHandler, int index, int x, int y) {
        super(itemHandler, index, x, y);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(getMaxStackSize(), stack.getMaxStackSize());
    }
}