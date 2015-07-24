package moze_intel.projecte.api.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IEMCProxy
{
    /**
     * Registers a custom EMC value for this ItemStack
     * Call this during any of the main loading phases (Preinit, Init, Postinit)
     * @param stack The stack we want to define EMC for
     * @param value The value to define. Values below 0 are changed to 0
     */
    void registerCustomEMC(ItemStack stack, int value);

    /**
     * Queries the EMC value registry if the given block has an EMC value
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param block The block we want to query
     * @return Whether the block has an emc value
     */
    boolean hasValue(Block block);

    /**
     * Queries the EMC value registry if the given item has an EMC value
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param item The item we want to query
     * @return Whether the item has an emc value
     */
    boolean hasValue(Item item);

    /**
     * Queries the EMC value registry if the given ItemStack has an EMC value
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * Note that this is simply a helper function, and is functionally equivalent to IEMCProxy.hasValue(stack.getItem())
     * @param stack The stack we want to query
     * @return Whether the ItemStack has an emc value
     */
    boolean hasValue(ItemStack stack);

    /**
     * Queries the EMC value for the provided block
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param block The block we want to query
     * @return The block's EMC value, or 0 if there is none
     */
    int getValue(Block block);

    /**
     * Queries the EMC value for the provided item
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param item The item we want to query
     * @return The item's EMC value, or 0 if there is none
     */
    int getValue(Item item);

    /**
     * Queries the EMC value for the provided stack
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * This takes into account bonuses such as stored emc in power items and enchantments
     * @param stack The stack we want to query
     * @return The stack's EMC value, or 0 if there is none
     */
    int getValue(ItemStack stack);
}
