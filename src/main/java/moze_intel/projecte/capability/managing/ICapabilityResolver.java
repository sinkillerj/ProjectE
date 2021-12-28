package moze_intel.projecte.capability.managing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public interface ICapabilityResolver<CAPABILITY> extends ICapabilityProvider {

	@Nonnull
	Capability<CAPABILITY> getMatchingCapability();

	@Nonnull
	<T> LazyOptional<T> getCapabilityUnchecked(@Nonnull Capability<T> capability, @Nullable Direction side);

	@Nonnull
	@Override
	default  <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
		if (capability == getMatchingCapability()) {
			return getCapabilityUnchecked(capability, side);
		}
		return LazyOptional.empty();
	}

	void invalidate(@Nonnull Capability<?> capability, @Nullable Direction side);

	void invalidateAll();
}