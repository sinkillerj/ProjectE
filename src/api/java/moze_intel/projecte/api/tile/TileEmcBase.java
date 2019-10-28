package moze_intel.projecte.api.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

/**
 * Base class for the reference implementations IEmcStorage
 *
 * Extend this if you want fine-grained control over all aspects of how your tile provides or accepts EMC
 *
 * @author williewillus
 */
public class TileEmcBase extends TileEntity implements IEmcStorage {

	private LazyOptional<IEmcStorage> emcStorageCapability = LazyOptional.of(() -> this);
	private long maximumEMC;
	private long currentEMC;

	protected TileEmcBase(TileEntityType<?> type) {
		super(type);
		setMaximumEMC(Long.MAX_VALUE);
	}

	public final void setMaximumEMC(long max) {
		maximumEMC = max;
		if (getStoredEmc() > getMaximumEmc()) {
			currentEMC = getMaximumEmc();
		}
	}

	@Override
	public long getStoredEmc() {
		return currentEMC;
	}

	@Override
	public long getMaximumEmc() {
		return maximumEMC;
	}

	/**
	 * @return The maximum amount of Emc that can be inserted at once into this {@link IEmcStorage}
	 */
	protected long getEmcInsertLimit() {
		return getNeededEmc();
	}

	/**
	 * @return The maximum amount of Emc that can be extracted at once from this {@link IEmcStorage}
	 */
	protected long getEmcExtractLimit() {
		return getStoredEmc();
	}

	/**
	 * Set this to false to stop this Emc tile from accepting Emc.
	 */
	protected boolean canAcceptEmc() {
		return true;
	}

	/**
	 * Set this to false to stop this Emc tile from being able to provide Emc.
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
		}
		return toAdd;
	}

	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT tag) {
		tag = super.write(tag);
		if (getStoredEmc() > getMaximumEmc()) {
			currentEMC = getMaximumEmc();
		}
		tag.putLong("EMC", getStoredEmc());
		return tag;
	}

	@Override
	public void read(@Nonnull CompoundNBT tag) {
		super.read(tag);
		long set = tag.getLong("EMC");
		if (set > getMaximumEmc()) {
			set = getMaximumEmc();
		}
		currentEMC = set;
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == ProjectEAPI.EMC_STORAGE_CAPABILITY) {
			return emcStorageCapability.cast();
		}
		return super.getCapability(cap, side);
	}
}