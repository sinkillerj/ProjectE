package moze_intel.projecte.gameObjs.container.slots;

import com.google.common.base.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

// Partial copy of SlotItemhandler with some tweaks to make it saner, and a validator
public class ValidatedSlot extends Slot {

    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    private final IItemHandler itemHandler;
    private final int index;
    private final Predicate<ItemStack> validator;

    public ValidatedSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> validator)
    {
        super(emptyInventory, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
        this.validator = validator;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        if (stack == null)
            return false;
        ItemStack remainder = this.getItemHandler().insertItem(index, stack, true);
        return remainder == null || remainder.stackSize < stack.stackSize;
    }

    @Override
    public ItemStack getStack()
    {
        return this.getItemHandler().getStackInSlot(index);
    }

    @Override
    public void putStack(ItemStack stack)
    {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(index, stack);
        this.onSlotChanged();
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        ItemStack maxAdd = stack.copy();
        maxAdd.stackSize = maxAdd.getMaxStackSize();
        ItemStack currentStack = this.getItemHandler().getStackInSlot(index);
        ItemStack remainder = this.getItemHandler().insertItem(index, maxAdd, true);

        int current = currentStack == null ? 0 : currentStack.stackSize;
        int added = maxAdd.stackSize - (remainder != null ? remainder.stackSize : 0);
        return current + added;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn)
    {
        return true; // PE: Remove testing itemhandler
    }

    @Override
    public ItemStack decrStackSize(int amount)
    {
        ItemStack stack = getItemHandler().getStackInSlot(index);
        if (stack == null)
            return null;
        else
        {
            ItemStack itemstack = stack.splitStack(amount);

            if (stack.stackSize == 0)
            {
                putStack(null);
            }

            return itemstack;
        }
    }

    public IItemHandler getItemHandler()
    {
        return itemHandler;
    }
}
