package moze_intel.projecte.capability;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Range;

public class EmcHolderItemCapabilityWrapper extends BasicItemCapability<IItemEmcHolder> implements IItemEmcHolder {

	@Override
	public Capability<IItemEmcHolder> getCapability() {
		return PECapabilities.EMC_HOLDER_ITEM_CAPABILITY;
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
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getStoredEmc(@Nonnull ItemStack stack) {
		return getItem().getStoredEmc(stack);
	}

	@Override
	@Range(from = 1, to = Long.MAX_VALUE)
	public long getMaximumEmc(@Nonnull ItemStack stack) {
		return getItem().getMaximumEmc(stack);
	}
}