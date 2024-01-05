package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

/**
 * @implNote Heavily based off of Mekanism's IInsertableSlot
 */
public interface IInsertableSlot {

	private Slot self() {
		return (Slot) this;
	}

	/**
	 * <p>
	 * Inserts an {@link ItemStack} into this {@link IInsertableSlot} and return the remainder as if a player was inserting by trying to interact with the slot. The
	 * {@link ItemStack} <em>should not</em> be modified in this function!
	 * </p>
	 * Note: This behaviour is subtly different from {@link net.neoforged.neoforge.fluids.capability.IFluidHandler#fill(net.neoforged.neoforge.fluids.FluidStack,
	 * net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction)}
	 *
	 * @param stack    {@link ItemStack} to insert. This must not be modified by the slot.
	 * @param simulate Whether to simulate insertion.
	 *
	 * @return The remaining {@link ItemStack} that was not inserted (if the entire stack is accepted, then return an empty {@link ItemStack}). May be the same as the
	 * input {@link ItemStack} if unchanged, otherwise a new {@link ItemStack}. The returned ItemStack can be safely modified after
	 */
	@NotNull
	default ItemStack insertItem(@NotNull ItemStack stack, boolean simulate) {
		Slot self = self();
		if (stack.isEmpty() || !self.mayPlace(stack)) {
			//"Fail quick" if the given stack is empty, or we are not valid for the slot
			return stack;
		}
		ItemStack current = self.getItem();
		int needed = self.getMaxStackSize(stack) - current.getCount();
		if (needed <= 0) {
			//Fail if we are a full slot
			return stack;
		}
		if (current.isEmpty() || ItemHandlerHelper.canItemStacksStack(current, stack)) {
			int toAdd = Math.min(stack.getCount(), needed);
			if (!simulate) {
				//If we want to actually insert the item, then update the current item
				//Set the stack to our new stack (we have no simple way to increment the stack size) so we have to set it instead of being able to just grow it
				self.set(stack.copyWithCount(current.getCount() + toAdd));
			}
			return ItemHelper.size(stack, stack.getCount() - toAdd);
		}
		//If we didn't accept this item, then just return the given stack
		return stack;
	}
}
