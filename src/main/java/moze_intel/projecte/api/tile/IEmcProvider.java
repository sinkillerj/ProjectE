package moze_intel.projecte.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Implement this interface to specify that "EMC can be taken from this Tile Entity from an external source"
 * The contract of this interface is only the above statement. Implementors must auto-pull from providers or auto-push to acceptors on their own
 * ProjectE implements an "active-push" system, where providers automatically send EMC to acceptors
 * Reference implementation provided in TileEmcHandler
 *
 * @author williewillus
 */
public interface IEMCProvider extends IEMCStorage
{
	/**
	 * Extract, at most, the given amount of EMC from the given side
	 * @param side The side to extract EMC from
	 * @param toExtract The maximum amount to extract
	 * @return The amount actually extracted
	 */
	double provideEMC(ForgeDirection side, double toExtract);
}
