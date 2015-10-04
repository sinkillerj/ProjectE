package moze_intel.projecte.api.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import moze_intel.projecte.api.exception.NoCreationEmcValueException;
import moze_intel.projecte.api.exception.NoDestructionEmcValueException;

import java.util.Map;

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
    void registerCustomEMC(Object o, int value);
    
    /**
     * @deprecated As of API version 8 use {@link #canBeCreatedWithEmc(ItemStack)} or {@link #canBeTurnedIntoEmc(ItemStack)} depending on your use case.
     */
    @Deprecated
    boolean hasValue(Block block);

    /**
     * @deprecated As of API version 8 use {@link #canBeCreatedWithEmc(ItemStack)} or {@link #canBeTurnedIntoEmc(ItemStack)} depending on your use case.
     */
    @Deprecated
    boolean hasValue(Item item);

    /**
     * @deprecated As of API version 8 use {@link #canBeCreatedWithEmc(ItemStack)} or {@link #canBeTurnedIntoEmc(ItemStack)} depending on your use case.
     */
    @Deprecated
    boolean hasValue(ItemStack stack);

    /**
     * @deprecated As of API version 8 use {@link #getCreationEmcCost(ItemStack)} or {@link #getDestructionEmc(ItemStack)} depending on your use case.
     */
    @Deprecated
    int getValue(Block block);

    /**
     * @deprecated As of API version 8 use {@link #getCreationEmcCost(ItemStack)} or {@link #getDestructionEmc(ItemStack)} depending on your use case.
     */
    @Deprecated
    int getValue(Item item);

    /**
     * @deprecated As of API version 8 use {@link #getCreationEmcCost(ItemStack)} or {@link #getDestructionEmc(ItemStack)} depending on your use case.
     */
    @Deprecated
    int getValue(ItemStack stack);

    /**
     * Querys the EMC Registry for the provided stack using ItemId/BlockId and Metadata/Damage.
     * Checks if the ItemStack can be created with EMC.
     * Can be called at any time, but will only return valid results if a world is loaded.
     * Can be called on both sides
     * @param stack The ItemStack containing the {@code Block} or {@code Item}
     * @return {@code true} if the stack can be created with EMC. Use {@link #getCreationEmcCost} to find out how much it should cost.
     * @see #getCreationEmcCost(ItemStack)
     */
    boolean canBeCreatedWithEmc(ItemStack stack);

    /**
     * Querys the EMC Registry for the provided stack using ItemId/BlockId and Metadata/Damage. Does not use stacksize!
     * Can only be called for ItemStacks for which {@link #canBeCreatedWithEmc(ItemStack)} returned {@code true}.
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param stack The ItemStack containing the {@code Block} or {@code Item}
     * @return The Amount of EMC that is consumed to create the ItemStack
     * @throws NoCreationEmcValueException when the provided {@code stack} can not be created with EMC, so {@link #canBeCreatedWithEmc(ItemStack)} returned {@code false}
     */
    int getCreationEmcCost(ItemStack stack) throws NoCreationEmcValueException;


    /**
     * Querys the EMC Registry for the provided stack using ItemId/BlockId and Metadata/Damage.
     * Checks if the ItemStack can be turned into EMC.
     * Can be called at any time, but will only return valid results if a world is loaded.
     * Can be called on both sides
     * @param stack The ItemStack containing the {@code Block} or {@code Item}
     * @return {@code true} if the stack can be turned into EMC. Use {@link #getDestructionEmc(ItemStack)} to find out how much it should yield.
     */
    boolean canBeTurnedIntoEmc(ItemStack stack);

    /**
     * Querys the EMC Registry for the provided stack using ItemId/BlockId and Metadata/Damage. Does not use stacksize!
     * Will apply a multiplier for damaged tools, bonus EMC for enchantments and also include EMC stored in the Item.
     * Can only be called for ItemStacks for which {@link #canBeTurnedIntoEmc(ItemStack)} returned {@code true}.
     * Can be called at any time, but will only return valid results if a world is loaded
     * Can be called on both sides
     * @param stack The ItemStack containing the {@code Block} or {@code Item}
     * @return The Amount of EMC that is generated when the ItemStack is consumed.
     * @throws NoDestructionEmcValueException when the provided {@code stack} can not be turned into EMC, so {@link #canBeTurnedIntoEmc(ItemStack)} returned {@code false}
     */
    int getDestructionEmc(ItemStack stack) throws NoDestructionEmcValueException;
}
