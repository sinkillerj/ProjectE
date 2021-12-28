package moze_intel.projecte.gameObjs.block_entities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.capability.managing.ICapabilityResolver;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class CapabilityTileEMC extends TileEmc {

	@Nullable
	protected ICapabilityResolver<IItemHandler> itemHandlerResolver;

	public CapabilityTileEMC(BlockEntityTypeRegistryObject<? extends CapabilityTileEMC> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public CapabilityTileEMC(BlockEntityTypeRegistryObject<? extends CapabilityTileEMC> type, BlockPos pos, BlockState state, long maxAmount) {
		super(type, pos, state, maxAmount);
	}

	@Override
	public void invalidateCaps() {
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