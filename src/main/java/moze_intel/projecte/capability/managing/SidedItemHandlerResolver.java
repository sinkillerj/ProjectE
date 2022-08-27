package moze_intel.projecte.capability.managing;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SidedItemHandlerResolver implements ICapabilityResolver<IItemHandler> {

	protected abstract ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction side);

	@NotNull
	@Override
	public Capability<IItemHandler> getMatchingCapability() {
		return ForgeCapabilities.ITEM_HANDLER;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapabilityUnchecked(@NotNull Capability<T> capability, @Nullable Direction side) {
		return getResolver(side).getCapabilityUnchecked(capability, side);
	}

	@Override
	public void invalidate(@NotNull Capability<?> capability, @Nullable Direction side) {
		getResolver(side).invalidate(capability, side);
	}
}