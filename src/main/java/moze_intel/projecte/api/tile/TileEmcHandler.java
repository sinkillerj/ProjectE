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
		this.maximumEMC = Double.MAX_VALUE;
	}

	public TileEmcHandler(double max)
	{
		this.maximumEMC = max;
	}

	// -- IEMCAcceptor -- //
	@Override
	public double acceptEMC(@Nonnull EnumFacing side, double toAccept)
	{
		double toAdd = Math.min(maximumEMC - currentEMC, toAccept);
		currentEMC += toAdd;
		return toAdd;
	}

	// -- IEMCProvider -- //
	@Override
	public double provideEMC(@Nonnull EnumFacing side, double toExtract)
	{
		double toRemove = Math.min(currentEMC, toExtract);
		currentEMC -= toRemove;
		return toRemove;
	}

	// -- IEMCStorage --//
	@Override
	public double getStoredEmc()
	{
		return currentEMC;
	}

	@Override
	public double getMaximumEmc()
	{
		return maximumEMC;
	}
}
