package moze_intel.projecte.capability.managing;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICapabilityResolver<CAPABILITY> extends ICapabilityProvider {

	@NotNull
	Capability<CAPABILITY> getMatchingCapability();

	@NotNull
	<T> LazyOptional<T> getCapabilityUnchecked(@NotNull Capability<T> capability, @Nullable Direction side);

	@NotNull
	@Override
	default  <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		if (capability == getMatchingCapability()) {
			return getCapabilityUnchecked(capability, side);
		}
		return LazyOptional.empty();
	}

	void invalidate(@NotNull Capability<?> capability, @Nullable Direction side);

	void invalidateAll();
}