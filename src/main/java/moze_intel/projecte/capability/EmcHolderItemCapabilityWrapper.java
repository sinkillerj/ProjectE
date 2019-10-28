package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage.EmcAction;
import moze_intel.projecte.capability.ItemCapabilityWrapper.ItemCapability;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

public class EmcHolderItemCapabilityWrapper extends ItemCapability<IItemEmcHolder> implements IItemEmcHolder {

	@Override
	protected Capability<IItemEmcHolder> getCapability() {
		return ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY;
	}

	@Override
	public long insertEmc(@Nonnull ItemStack stack, long toInsert, EmcAction action) {
		return getItem().insertEmc(stack, toInsert, action);
	}

	@Override
	public long extractEmc(@Nonnull ItemStack stack, long toExtract, EmcAction action) {
		return getItem().extractEmc(stack, toExtract, action);
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