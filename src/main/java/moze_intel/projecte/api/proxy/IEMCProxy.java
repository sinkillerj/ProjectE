package moze_intel.projecte.api.proxy;

import net.minecraft.item.ItemStack;

public interface IEMCProxy
{
    /**
     * Registers a custom EMC value for this ItemStack
     * Call this during any of the main loading phases (Preinit, Init, Postinit)
     * @param stack The stack we want to define EMC for
     * @param value The value to define. Values below 0 are changed to 0
     */
    void registerCustomEmc(ItemStack stack, int value);

    /**
     * Queries the EMC value registry if the object has an EMC value
     * Can be called at any time, but will only return valid results if a world is loaded
     * @param obj A Block, Item, or ItemStack we want to query
     * @return Whether the object has an emc value
     */
    boolean hasValue(Object obj);

    /**
     * Queries the EMC value for the provided object
     * Can be called at any time, but will only return valid results if a world is loaded
     * @param obj A Block, Item, or ItemStack we want to query
     * @return The object's EMC value, or 0 if there is none
     */
    int getValue(Object obj);
}
