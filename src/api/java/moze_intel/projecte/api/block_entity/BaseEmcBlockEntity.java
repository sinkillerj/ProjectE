package moze_intel.projecte.api.block_entity;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Base class for the reference implementations IEmcStorage
 * <p>
 * Extend this if you want fine-grained control over all aspects of how your block entity provides or accepts EMC
 *
 * @author williewillus
 */
public class BaseEmcBlockEntity extends BlockEntity implements IEmcStorage {

	/**
	 * To expose the EMC Storage capability this provider or one similar should be registered for your block entity inside of
	 * {@link net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent}
	 */
	public static final ICapabilityProvider<BaseEmcBlockEntity, @Nullable Direction, IEmcStorage> EMC_STORAGE_PROVIDER = (blockEntity, context) -> blockEntity;

	private long maximumEMC;
	private long currentEMC;

	protected BaseEmcBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		setMaximumEMC(Long.MAX_VALUE);
	}

	public final void setMaximumEMC(@Range(from = 1, to = Long.MAX_VALUE) long max) {
		maximumEMC = max;
		if (getStoredEmc() > getMaximumEmc()) {
			currentEMC = getMaximumEmc();
			storedEmcChanged();
		}
	}

	@Override
	@Range(from = 0, to = Long.MAX_VALUE)
	public long getStoredEmc() {
		return currentEMC;
	}

	@Override
	@Range(from = 1, to = Long.MAX_VALUE)
	public long getMaximumEmc() {
		return maximumEMC;
	}

	/**
	 * @return The maximum amount of Emc that can be inserted at once into this {@link IEmcStorage}
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	protected long getEmcInsertLimit() {
		return getNeededEmc();
	}

	/**
	 * @return The maximum amount of Emc that can be extracted at once from this {@link IEmcStorage}
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	protected long getEmcExtractLimit() {
		return getStoredEmc();
	}

	/**
	 * Set this to false to stop this Emc block entity from accepting Emc.
	 */
	protected boolean canAcceptEmc() {
		return true;
	}

	/**
	 * Set this to false to stop this Emc block entity from being able to provide Emc.
	 */
	protected boolean canProvideEmc() {
		return true;
	}

	@Override
	public long extractEmc(long toExtract, EmcAction action) {
		if (toExtract < 0) {
			return insertEmc(-toExtract, action);
		}
		if (canProvideEmc()) {
			return forceExtractEmc(Math.min(getEmcExtractLimit(), toExtract), action);
		}
		return 0;
	}

	@Override
	public long insertEmc(long toAccept, EmcAction action) {
		if (toAccept < 0) {
			return extractEmc(-toAccept, action);
		}
		if (canAcceptEmc()) {
			return forceInsertEmc(Math.min(getEmcInsertLimit(), toAccept), action);
		}
		return 0;
	}

	/**
	 * Similar to {@link IEmcStorage#extractEmc(long, EmcAction)} except, it is an internal method for use of removing EMC except it ignores if we can provide EMC
	 * externally or not.
	 *
	 * @param toExtract The maximum amount to extract
	 * @param action    The action to perform, either {@link EmcAction#EXECUTE} or {@link EmcAction#SIMULATE}
	 *
	 * @return The amount actually accepted
	 *
	 * @apiNote For internal use this rather than {@link IEmcStorage#extractEmc(long, EmcAction)}, as it will probably behave more as expected.
	 */
	protected long forceExtractEmc(long toExtract, EmcAction action) {
		if (toExtract < 0) {
			return forceInsertEmc(-toExtract, action);
		}
		long toRemove = Math.min(getStoredEmc(), toExtract);
		if (action.execute()) {
			currentEMC -= toRemove;
			storedEmcChanged();
		}
		return toRemove;
	}

	/**
	 * Similar to {@link IEmcStorage#insertEmc(long, EmcAction)} except, it is an internal method for use of adding EMC except it ignores if we can accept EMC externally
	 * or not, and instead of handling negative values it just acts as if zero was passed.
	 *
	 * @param toAccept The maximum amount to accept
	 * @param action   The action to perform, either {@link EmcAction#EXECUTE} or {@link EmcAction#SIMULATE}
	 *
	 * @return The amount actually accepted
	 *
	 * @apiNote For internal use this rather than {@link IEmcStorage#insertEmc(long, EmcAction)}, as it will probably behave more as expected.
	 */
	protected long forceInsertEmc(long toAccept, EmcAction action) {
		if (toAccept < 0) {
			return forceExtractEmc(-toAccept, action);
		}
		long toAdd = Math.min(getNeededEmc(), toAccept);
		if (action.execute()) {
			currentEMC += toAdd;
			storedEmcChanged();
		}
		return toAdd;
	}

	/**
	 * Called when the amount of EMC stored changes.
	 */
	protected void storedEmcChanged() {
		setChanged();
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		if (getStoredEmc() > getMaximumEmc()) {
			currentEMC = getMaximumEmc();
		}
		tag.putLong("EMC", getStoredEmc());
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		long set = tag.getLong("EMC");
		if (set > getMaximumEmc()) {
			set = getMaximumEmc();
		}
		currentEMC = set;
	}
}