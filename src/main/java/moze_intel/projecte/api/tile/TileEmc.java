package moze_intel.projecte.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Reference implementation of IEmcAcceptor, IEmcProvider, and IEmcStorage
 *
 * @author williewillus
 */
public class TileEmc implements IEmcAcceptor, IEmcProvider
{
	protected double maximumAmount = Double.MAX_VALUE;
	protected double currentAmount = 0;

	// -- IEmcAcceptor -- //
	@Override
	public double acceptEmc(ForgeDirection side, double toAccept)
	{
		double toAdd = Math.min(maximumAmount - currentAmount, toAccept);
		currentAmount += toAdd;
		return toAdd;
	}

	// -- IEmcProvider -- //
	@Override
	public double extractEmc(ForgeDirection side, double toExtract)
	{
		double toRemove = Math.min(currentAmount, toExtract);
		currentAmount -= toRemove;
		return toRemove;
	}

	// -- IEmcStorage --//
	@Override
	public double getStoredEmc()
	{
		return currentAmount;
	}

	@Override
	public double getMaximumEmc()
	{
		return maximumAmount;
	}
}
