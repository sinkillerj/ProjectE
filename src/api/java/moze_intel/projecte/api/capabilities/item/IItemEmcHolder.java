package moze_intel.projecte.api.capabilities.item;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * This interface defines the contract for items that wish to expose their internal EMC storage for external manipulation
 *
 * This is exposed through the Capability system.
 *
 * Acquire an instance of this using {@link ItemStack#getCapability(Capability, Direction)}.
 *
 * @author williewillus
 */
public interface IItemEmcHolder
{
	/**
	 * Adds EMC to the itemstack
	 * @param stack The itemstack to add to
	 * @param toAdd The maximum amount to add
	 * @return The amount that was actually added
	 */
	long addEmc(@Nonnull ItemStack stack, long toAdd);

	/**
	 * Extracts EMC from the itemstack
	 * @param stack The itemstack to remove from
	 * @param toRemove The maximum amount to remove
	 * @return The amount that was actually extracted
	 */
	long extractEmc(@Nonnull ItemStack stack, long toRemove);

	/**
	 * Gets the current EMC this stack is showing to the public
	 * @param stack The stack to query
	 * @return The current publicly-accessible EMC stored in this stack
	 */
	long getStoredEmc(@Nonnull ItemStack stack);

	/**
	 * Gets the maximum EMC that is allowed to be stored in this stack
	 * @param stack The stack to query
	 * @return The maximum amount of publicly-accessible EMC that can be stored in this stack
	 * @implNote Must be greater than zero
	 */
	long getMaximumEmc(@Nonnull ItemStack stack);
}
