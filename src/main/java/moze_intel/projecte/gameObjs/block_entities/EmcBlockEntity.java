package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.api.block_entity.BaseEmcBlockEntity;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public abstract class EmcBlockEntity extends BaseEmcBlockEntity {

	private boolean updateComparators;
	private long lastSave;

	public EmcBlockEntity(BlockEntityTypeRegistryObject<? extends EmcBlockEntity> type, BlockPos pos, BlockState state) {
		this(type, pos, state, Long.MAX_VALUE);
	}

	public EmcBlockEntity(BlockEntityTypeRegistryObject<? extends EmcBlockEntity> type, BlockPos pos, BlockState state,
			@Range(from = 1, to = Long.MAX_VALUE) long maxAmount) {
		super(type.get(), pos, state);
		setMaximumEMC(maxAmount);
	}

	protected void updateComparators(@NotNull Level level, @NotNull BlockPos pos) {
		//Only update the comparator state if we need to update comparators
		//Note: We call this at the end of child implementations to try and update any changes immediately instead
		// of them having to be delayed a tick
		if (updateComparators) {
			BlockState state = getBlockState();
			if (!state.isAir()) {
				level.updateNeighbourForOutputSignal(pos, state.getBlock());
			}
			updateComparators = false;
		}
	}

	protected boolean emcAffectsComparators() {
		return false;
	}

	@Override
	protected void storedEmcChanged() {
		if (level != null) {
			markDirty(level, worldPosition, emcAffectsComparators());
		}
	}

	@Override
	public final void setChanged() {
		if (level != null) {
			markDirty(level, worldPosition, true);
		}
	}

	public void markDirty(@NotNull Level level, @NotNull BlockPos pos, boolean recheckComparators) {
		//Copy of the base impl of markDirty in BlockEntity, except only updates comparator state when something changed
		// and if our block supports having a comparator signal, instead of always doing it
		long time = level.getGameTime();
		if (lastSave != time) {
			//Only mark the chunk as dirty at most once per tick
			ChunkAccess chunk = level.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
			if (chunk != null) {
				chunk.setUnsaved(true);
			}
			lastSave = time;
		}
		if (recheckComparators && !level.isClientSide) {
			updateComparators = true;
		}
	}

	@NotNull
	@Override
	public final CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
		//TODO: Eventually it would be nice to try and minimize how much data we send in the update tags
		return saveWithoutMetadata(registries);
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
	protected long sendToAllAcceptors(@NotNull Level level, BlockPos pos, long emc) {
		if (emc == 0 || !canProvideEmc()) {
			//If we cannot provide emc then just return
			return 0;
		}
		emc = Math.min(getEmcExtractLimit(), emc);
		long sentEmc = 0;
		//There are at most 6 adjacent directions, use a fixed size array to avoid allocating a list each tick
		IEmcStorage[] targets = new IEmcStorage[6];
		int targetCount = 0;
		for (Direction dir : Constants.DIRECTIONS) {
			//Make sure the neighboring block is loaded as if we are on a chunk border on the edge of loaded chunks this may not be the case
			IEmcStorage theirEmcStorage = WorldHelper.getCapability(level, PECapabilities.EMC_STORAGE_CAPABILITY, pos.relative(dir), dir.getOpposite());
			if (theirEmcStorage != null) {
				if (!isRelay() || !theirEmcStorage.isRelay()) {
					//If they are both relays don't add the pairing to prevent thrashing
					if (theirEmcStorage.insertEmc(1, EmcAction.SIMULATE) > 0) {
						//If they are wiling to accept any Emc then we consider them to be an "acceptor"
						targets[targetCount++] = theirEmcStorage;
					}
				}
			}
		}

		if (targetCount > 0) {
			long emcPer = emc / targetCount;
			for (int i = 0; i < targetCount; i++) {
				IEmcStorage target = targets[i];
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
			empty = true;
			for (int slot = 0, slots = getSlots(); slot < slots; slot++) {
				if (!getStackInSlot(slot).isEmpty()) {
					empty = false;
					break;
				}
			}
		}

		/**
		 * @apiNote Only use this on the server
		 */
		public boolean isEmpty() {
			return empty;
		}
	}
}