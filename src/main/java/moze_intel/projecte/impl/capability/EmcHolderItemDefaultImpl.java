package moze_intel.projecte.impl.capability;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.item.ItemStack;

public final class EmcHolderItemDefaultImpl implements IItemEmcHolder {

	@Override
	public long addEmc(@Nonnull ItemStack stack, long toAdd) {
		long add = Math.min(getMaximumEmc(stack) - getStoredEmc(stack), toAdd);
		ItemPE.addEmcToStack(stack, add);
		return add;
	}

	@Override
	public long extractEmc(@Nonnull ItemStack stack, long toRemove) {
		long sub = Math.min(getStoredEmc(stack), toRemove);
		ItemPE.removeEmc(stack, sub);
		return sub;
	}

	@Override
	public long getStoredEmc(@Nonnull ItemStack stack) {
		return ItemPE.getEmc(stack);
	}

	@Override
	public long getMaximumEmc(@Nonnull ItemStack stack) {
		return 1;
	}
}