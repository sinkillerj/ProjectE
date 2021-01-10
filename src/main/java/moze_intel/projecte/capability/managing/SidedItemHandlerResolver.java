package moze_intel.projecte.capability.managing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class SidedItemHandlerResolver implements ICapabilityResolver<IItemHandler> {

	protected abstract ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction side);

	@Nonnull
	@Override
	public Capability<IItemHandler> getMatchingCapability() {
		return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapabilityUnchecked(@Nonnull Capability<T> capability, @Nullable Direction side) {
		return getResolver(side).getCapabilityUnchecked(capability, side);
	}

	@Override
	public void invalidate(@Nonnull Capability<?> capability, @Nullable Direction side) {
		getResolver(side).invalidate(capability, side);
	}
}