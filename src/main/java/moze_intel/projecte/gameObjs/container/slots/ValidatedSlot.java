package moze_intel.projecte.gameObjs.container.slots;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Predicate;

// Partial copy of SlotItemhandler with some tweaks to make it saner, and a validator
public class ValidatedSlot extends SlotItemHandler {

    private final Predicate<ItemStack> validator;

    public ValidatedSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> validator)
    {
        super(itemHandler, index, xPosition, yPosition);
        this.validator = validator;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return super.isItemValid(stack) && validator.test(stack);
    }

}
