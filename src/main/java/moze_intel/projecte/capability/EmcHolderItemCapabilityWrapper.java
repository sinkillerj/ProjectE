package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.capability.ItemCapabilityWrapper.ItemCapability;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

public class EmcHolderItemCapabilityWrapper extends ItemCapability<IItemEmcHolder> implements IItemEmcHolder {

	@Override
	protected Capability<IItemEmcHolder> getCapability() {
		return ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY;
	}

	@Override
	public long addEmc(@Nonnull ItemStack stack, long toAdd) {
		return getItem().addEmc(stack, toAdd);
	}

	@Override
	public long extractEmc(@Nonnull ItemStack stack, long toRemove) {
		return getItem().extractEmc(stack, toRemove);
	}

	@Override
	public long getStoredEmc(@Nonnull ItemStack stack) {
		return getItem().getStoredEmc(stack);
	}

	@Override
	public long getMaximumEmc(@Nonnull ItemStack stack) {
		return getItem().getMaximumEmc(stack);
	}
}