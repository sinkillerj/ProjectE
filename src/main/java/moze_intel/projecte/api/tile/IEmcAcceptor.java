package moze_intel.projecte.api.tile;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Defines the contract for Tile Entities that can accept EMC from the public
 * Reference implementation provided in TileEmc
 *
 * @author williewillus
 */
public interface IEmcAcceptor extends IEmcStorage
{
	/**
	 * Accept, at most, the given amount of EMC from the given side
	 * @param side The side to accept EMC from
	 * @param toAccept The maximum amount to accept
	 * @return The amount actually accepted
	 */
	double acceptEmc(ForgeDirection side, double toAccept);
}
