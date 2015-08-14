package moze_intel.projecte.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Reference implementation of IEMCAcceptor
 *
 * @author williewillus
 */
public class TileEMCAcceptor extends TileEMCBase implements IEMCAcceptor
{
	@Override
	public double acceptEMC(ForgeDirection side, double toAccept)
	{
		double toAdd = Math.min(maximumEMC - currentEMC, toAccept);
		addEMC(toAdd);
		return toAdd;
	}
}
