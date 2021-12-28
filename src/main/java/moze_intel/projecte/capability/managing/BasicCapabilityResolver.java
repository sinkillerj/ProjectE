package moze_intel.projecte.capability.managing;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

/**
 * Simplified/modified version of Mekanism's BasicCapabilityResolver
 */
public abstract class BasicCapabilityResolver<CAPABILITY> implements ICapabilityResolver<CAPABILITY> {

	public static ICapabilityResolver<IItemHandler> getBasicItemHandlerResolver(NonNullSupplier<IItemHandler> supplier) {
		return new BasicCapabilityResolver<>(supplier) {
			@Nonnull
			@Override
			public Capability<IItemHandler> getMatchingCapability() {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
			}
		};
	}

	public static ICapabilityResolver<IItemHandler> getBasicItemHandlerResolver(IItemHandler handler) {
		return new BasicCapabilityResolver<>(handler) {
			@Nonnull
			@Override
			public Capability<IItemHandler> getMatchingCapability() {
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
			}
		};
	}

	private final NonNullSupplier<CAPABILITY> supplier;
	private LazyOptional<CAPABILITY> cachedCapability;

	protected BasicCapabilityResolver(CAPABILITY constant) {
		this.supplier = () -> constant;
	}

	protected BasicCapabilityResolver(NonNullSupplier<CAPABILITY> supplier) {
		this.supplier = supplier instanceof NonNullLazy ? supplier : NonNullLazy.of(supplier);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapabilityUnchecked(@Nonnull Capability<T> capability, @Nullable Direction side) {
		if (cachedCapability == null || !cachedCapability.isPresent()) {
			//If the capability has not been retrieved yet or it is not valid then recreate it
			cachedCapability = LazyOptional.of(supplier);
		}
		return cachedCapability.cast();
	}

	@Override
	public void invalidate(@Nonnull Capability<?> capability, @Nullable Direction side) {
		//We only have one capability so just invalidate everything
		invalidateAll();
	}

	@Override
	public void invalidateAll() {
		if (cachedCapability != null && cachedCapability.isPresent()) {
			cachedCapability.invalidate();
			cachedCapability = null;
		}
	}
}