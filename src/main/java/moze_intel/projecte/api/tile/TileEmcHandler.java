package moze_intel.projecte.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Reference implementation of both IEMCAcceptor and IEMCProvider
 *
 * @author williewillus
 */
public class TileEMCHandler extends TileEMCBase implements IEMCAcceptor, IEMCProvider
{
	public TileEMCHandler()
	{
		this.maximumEMC = Double.MAX_VALUE;
	}

	public TileEMCHandler(double max)
	{
		this.maximumEMC = max;
	}

	// -- IEMCAcceptor -- //
	@Override
	public double acceptEMC(ForgeDirection side, double toAccept)
	{
		double toAdd = Math.min(maximumEMC - currentEMC, toAccept);
		currentEMC += toAdd;
		return toAdd;
	}

	// -- IEMCProvider -- //
	@Override
	public double provideEMC(ForgeDirection side, double toExtract)
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
