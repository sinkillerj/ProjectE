package moze_intel.projecte.api.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;

public interface IEMCProxy
{
    /**
     * Registers a custom EMC value for this ItemStack
     * Call this during any of the main loading phases (Preinit, Init, Postinit)
     * @param stack The stack we want to define EMC for
     * @param value The value to define. Values below 0 are changed to 0
     * @deprecated Since ProjectE API version 1.1.0
     */
    @Deprecated
    default void registerCustomEMC(@Nonnull ItemStack stack, int value) {
        registerCustomEMC(stack, (long) value);
    }

    /**
     * Register a custom EMC value for emc calculation that is used in Recipes.
     * You can use the following things for the {@code o}-Parameter:
     * <ul>
     *     <li>{@link ItemStack} - The Modname:unlocalizedName and Metadata will be used to identify this ItemStack (May contain a {@code Block} or {@code Item})</li>
     *     <li>{@link String} - will be interpreted as an OreDictionary name.</li>
     *     <li>{@link Object} - (No subclasses of {@code Object} - only {@code Object}!) can be used as a intermediate fake object for complex recipes.</li>
     * </ul>
     * @param o
     * @param value
     * @see IConversionProxy#addConversion(int, Object, Map)
     * @deprecated Since ProjectE API version 1.1.0
     */
    @Deprecated
    default void registerCustomEMC(@Nonnull Object o, int value) {
        registerCustomEMC(o, (long) value);
    }

    /**
     * Registers a custom EMC value for this ItemStack
     * Call this during any of the main loading phases (Preinit, Init, Postinit)
     * @param stack The stack we want to define EMC for
     * @param value The value to define. Values below 0 are changed to 0
     */
    void registerCustomEMC(@Nonnull ItemStack stack, long value);

    /**
     * Register a custom EMC value for emc calculation that is used in Recipes.
     * You can use the following things for the {@code o}-Parameter:
     * <ul>
     *     <li>{@link ItemStack} - The Modname:unlocalizedName and Metadata will be used to identify this ItemStack (May contain a {@code Block} or {@code Item})</li>
     *     <li>{@link String} - will be interpreted as an OreDictionary name.</li>
     *     <li>{@link Object} - (No subclasses of {@code Object} - only {@code Object}!) can be used as a intermediate fake object for complex recipes.</li>
     * </ul>
     * @param o
     * @param value
     * @see IConversionProxy#addConversion(int, Object, Map)
     */
    void registerCustomEMC(@Nonnull Object o, long value);
    
    /**
     * Queries the EMC value registry if the given block has an EMC value
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param block The block we want to query
     * @return Whether the block has an emc value
     */
    boolean hasValue(@Nonnull Block block);

    /**
     * Queries the EMC value registry if the given item with a damage value of 0 has an EMC value
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param item The item we want to query
     * @return Whether the item has an emc value
     */
    boolean hasValue(@Nonnull Item item);

    /**
     * Queries the EMC value registry if the given ItemStack has an EMC value
     * This will also use the damage value to check if the Item has an EMC value
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param stack The stack we want to query
     * @return Whether the ItemStack has an emc value
     */
    boolean hasValue(@Nonnull ItemStack stack);

    /**
     * Queries the EMC value for the provided block
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param block The block we want to query
     * @return The block's EMC value, or 0 if there is none
     */
    long getValue(@Nonnull Block block);

    /**
     * Queries the EMC value for the provided item
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param item The item we want to query
     * @return The item's EMC value, or 0 if there is none
     */
    long getValue(@Nonnull Item item);

    /**
     * Queries the EMC value for the provided stack
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * This takes into account bonuses such as stored emc in power items and enchantments
     * @param stack The stack we want to query
     * @return The stack's EMC value, or 0 if there is none
     */
    long getValue(@Nonnull ItemStack stack);

    /**
     * Queries the EMC sell-value for the provided stack
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param stack The stack we want to query
     * @return EMC the stack should yield when burned by transmutation, condensers, or relays
     */
    long getSellValue(@Nonnull ItemStack stack);
}
