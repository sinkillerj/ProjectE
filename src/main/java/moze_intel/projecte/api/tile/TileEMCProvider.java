package moze_intel.projecte.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Reference implementation for IEMCProvider
 *
 * @author williewillus
 */
public class TileEMCProvider extends TileEMCBase implements IEMCProvider
{
	@Override
	public double provideEMC(ForgeDirection side, double toExtract)
	{
		double toRemove = Math.min(currentEMC, toExtract);
		removeEMC(toRemove);
		return toRemove;
	}
}
