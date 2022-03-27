package moze_intel.projecte.gameObjs.block_entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.block_entity.BaseEmcBlockEntity;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Range;

public abstract class EmcBlockEntity extends BaseEmcBlockEntity {

	private boolean updateComparators;

	public EmcBlockEntity(BlockEntityTypeRegistryObject<? extends EmcBlockEntity> type, BlockPos pos, BlockState state) {
		this(type, pos, state, Constants.BLOCK_ENTITY_MAX_EMC);
	}

	public EmcBlockEntity(BlockEntityTypeRegistryObject<? extends EmcBlockEntity> type, BlockPos pos, BlockState state,
			@Range(from = 1, to = Long.MAX_VALUE) long maxAmount) {
		super(type.get(), pos, state);
		setMaximumEMC(maxAmount);
	}

	protected void updateComparators() {
		//Only update the comparator state if we need to update comparators
		//Note: We call this at the end of child implementations to try and update any changes immediately instead
		// of them having to be delayed a tick
		if (updateComparators) {
			BlockState state = getBlockState();
			if (!state.isAir()) {
				level.updateNeighbourForOutputSignal(worldPosition, state.getBlock());
			}
			updateComparators = false;
		}
	}

	protected boolean emcAffectsComparators() {
		return false;
	}

	@Override
	protected void storedEmcChanged() {
		markDirty(emcAffectsComparators());
	}

	@Override
	public void setChanged() {
		markDirty(true);
	}

	public void markDirty(boolean recheckComparators) {
		//Copy of the base impl of markDirty in BlockEntity, except only updates comparator state when something changed
		// and if our block supports having a comparator signal, instead of always doing it
		if (level != null) {
			if (level.hasChunkAt(worldPosition)) {
				level.getChunkAt(worldPosition).setUnsaved(true);
			}
			if (recheckComparators && !level.isClientSide) {
				updateComparators = true;
			}
		}
	}

	@Nonnull
	@Override
	public final CompoundTag getUpdateTag() {
		//TODO: Eventually it would be nice to try and minimize how much data we send in the update tags
		return saveWithoutMetadata();
	}

	@Override
	public final ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/**
	 * The amount provided will be divided and evenly distributed as best as possible between adjacent IEmcStorage. This is limited also by our max extract limit
	 *
	 * @param emc The maximum combined emc to send to others
	 *
	 * @return The amount of Emc we actually sent
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	protected long sendToAllAcceptors(long emc) {
		if (level == null || !canProvideEmc()) {
			//If we cannot provide emc then just return
			return 0;
		}
		emc = Math.min(getEmcExtractLimit(), emc);
		long sentEmc = 0;
		List<IEmcStorage> targets = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			BlockPos neighboringPos = worldPosition.relative(dir);
			//Make sure the neighboring block is loaded as if we are on a chunk border on the edge of loaded chunks this may not be the case
			if (level.isLoaded(neighboringPos)) {
				BlockEntity neighboringBE = WorldHelper.getBlockEntity(level, neighboringPos);
				if (neighboringBE != null) {
					neighboringBE.getCapability(PECapabilities.EMC_STORAGE_CAPABILITY, dir.getOpposite()).ifPresent(theirEmcStorage -> {
						if (!isRelay() || !theirEmcStorage.isRelay()) {
							//If they are both relays don't add the pairing so as to prevent thrashing
							if (theirEmcStorage.insertEmc(1, EmcAction.SIMULATE) > 0) {
								//If they would be wiling to accept any Emc then we consider them to be an "acceptor"
								targets.add(theirEmcStorage);
							}
						}
					});
				}
			}
		}

		if (!targets.isEmpty()) {
			long emcPer = emc / targets.size();
			for (IEmcStorage target : targets) {
				long emcCanProvide = extractEmc(emcPer, EmcAction.SIMULATE);
				long acceptedEmc = target.insertEmc(emcCanProvide, EmcAction.EXECUTE);
				extractEmc(acceptedEmc, EmcAction.EXECUTE);
				sentEmc += acceptedEmc;
			}
		}
		return sentEmc;
	}

	protected class StackHandler extends ItemStackHandler {

		protected StackHandler(int size) {
			super(size);
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			setChanged();
		}
	}

	protected class CompactableStackHandler extends StackHandler {

		//Start as needing to check for compacting when loaded
		private boolean needsCompacting = true;
		private boolean empty;

		protected CompactableStackHandler(int size) {
			super(size);
		}

		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			needsCompacting = true;
		}

		public void compact() {
			if (needsCompacting) {
				if (level != null && !level.isClientSide) {
					empty = ItemHelper.compactInventory(this);
				}
				needsCompacting = false;
			}
		}

		@Override
		protected void onLoad() {
			super.onLoad();
			empty = IntStream.range(0, getSlots()).allMatch(slot -> getStackInSlot(slot).isEmpty());
		}

		/**
		 * @apiNote Only use this on the server
		 */
		public boolean isEmpty() {
			return empty;
		}
	}
}