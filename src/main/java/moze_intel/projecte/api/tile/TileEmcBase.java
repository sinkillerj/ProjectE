package moze_intel.projecte.api.tile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

/**
 * Base class for the reference implementations TileEmcProvider, TileEmcAcceptor, and TileEmcHandler
 * Usually you want to use one of three derived reference implementations
 * Extend this if you want fine-grained control over all aspects of how your tile provides or accepts EMC
 *
 * @author williewillus
 */
public class TileEmcBase extends TileEntity implements IEmcStorage
{
	protected long maximumEMC;
	protected long currentEMC = 0;

	protected TileEmcBase(TileEntityType<?> type)
	{
		super(type);
		setMaximumEMC(Long.MAX_VALUE);
	}

	public final void setMaximumEMC(long max)
	{
		maximumEMC = max;
		if (currentEMC > maximumEMC)
		{
			currentEMC = maximumEMC;
		}
	}

	@Override
	public long getStoredEmc()
	{
		return currentEMC;
	}

	@Override
	public long getMaximumEmc()
	{
		return maximumEMC;
	}

	/**
	 * Add EMC directly into the internal buffer. Use for internal implementation of your tile
	 */
	protected void addEMC(long toAdd)
	{
		currentEMC += toAdd;
		if (currentEMC > maximumEMC)
		{
			currentEMC = maximumEMC;
		}
	}

	/**
	 * Removes EMC directly into the internal buffer. Use for internal implementation of your tile
	 */
	protected void removeEMC(long toRemove)
	{
		currentEMC -= toRemove;
		if (currentEMC < 0)
		{
			currentEMC = 0;
		}
	}

	@Nonnull
	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		tag = super.write(tag);
		if (currentEMC > maximumEMC)
		{
			currentEMC = maximumEMC;
		}
		tag.putLong("EMC", currentEMC);
		return tag;
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);
		long set = tag.getLong("EMC");
		if (set > maximumEMC)
		{
			set = maximumEMC;
		}
		currentEMC = set;
	}
}
