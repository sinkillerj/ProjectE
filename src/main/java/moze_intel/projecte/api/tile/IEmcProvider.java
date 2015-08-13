package moze_intel.projecte.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Defines the contract for Tile Entities that can provide EMC to the public
 * Reference implementation provided in TileEmc
 *
 * @author williewillus
 */
public interface IEmcProvider extends IEmcStorage
{
	/**
	 * Extract, at most, the given amount of EMC from the given side
	 * @param side The side to extract EMC from
	 * @param toExtract The maximum amount to extract
	 * @return The amount actually extracted
	 */
	double extractEmc(ForgeDirection side, double toExtract);
}
