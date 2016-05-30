package moze_intel.projecte.api.item;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This interface defines the contract for items that wish to expose their internal EMC storage for external manipulation
 *
 * @author williewillus
 */
public interface IItemEmc
{
	/**
	 * Adds EMC to the itemstack
	 * @param stack The itemstack to add to
	 * @param toAdd The maximum amount to add
	 * @return The amount that was actually added
	 */
	double addEmc(@Nonnull ItemStack stack, double toAdd);

	/**
	 * Extracts EMC from the itemstack
	 * @param stack The itemstack to remove from
	 * @param toRemove The maximum amount to remove
	 * @return The amount that was actually extracted
	 */
	double extractEmc(@Nonnull ItemStack stack, double toRemove);

	/**
	 * Gets the current EMC this stack is showing to the public
	 * @param stack The stack to query
	 * @return The current publicly-accessible EMC stored in this stack
	 */
	double getStoredEmc(@Nonnull ItemStack stack);

	/**
	 * Gets the maximum EMC that is allowed to be stored in this stack
	 * @param stack The stack to query
	 * @return The maximum amount of publicly-accessible EMC that can be stored in this stack
	 */
	double getMaximumEmc(@Nonnull ItemStack stack);
}
