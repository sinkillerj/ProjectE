package moze_intel.projecte.api.tile;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

/**
 * Reference implementation of both IEMCAcceptor and IEMCProvider
 *
 * @author williewillus
 */
public class TileEmcHandler extends TileEmcBase implements IEmcAcceptor, IEmcProvider
{
	public TileEmcHandler()
	{
		this.maximumEMC = Long.MAX_VALUE;
	}

	public TileEmcHandler(long max)
	{
		this.maximumEMC = max;
	}

	// -- IEMCAcceptor -- //
	@Override
	public long acceptEMC(@Nonnull EnumFacing side, long toAccept)
	{
		long toAdd = Math.min(maximumEMC - currentEMC, toAccept);
		currentEMC += toAdd;
		return toAdd;
	}

	// -- IEMCProvider -- //
	@Override
	public long provideEMC(@Nonnull EnumFacing side, long toExtract)
	{
		long toRemove = Math.min(currentEMC, toExtract);
		currentEMC -= toRemove;
		return toRemove;
	}

	// -- IEMCStorage --//
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
}
