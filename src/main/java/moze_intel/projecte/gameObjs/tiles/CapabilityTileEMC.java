package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.capability.managing.ICapabilityResolver;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class CapabilityTileEMC extends TileEmc {

	@Nullable
	protected ICapabilityResolver<IItemHandler> itemHandlerResolver;

	public CapabilityTileEMC(TileEntityType<?> type) {
		super(type);
	}

	public CapabilityTileEMC(TileEntityType<?> type, long maxAmount) {
		super(type, maxAmount);
	}

	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		if (itemHandlerResolver != null) {
			//Should never be null but just in case
			itemHandlerResolver.invalidateAll();
		}
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandlerResolver != null) {
			//Should never be null but just in case
			return itemHandlerResolver.getCapabilityUnchecked(cap, side);
		}
		return super.getCapability(cap, side);
	}
}