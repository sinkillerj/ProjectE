package moze_intel.projecte.api.tile;

import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

/**
 * Implement this interface to specify that "EMC can be taken from this Tile Entity from an external source"
 * The contract of this interface is limited to only the above statement
 * However, ProjectE implements an "active-push" system, where providers automatically send EMC to acceptors. You are recommended to follow this convention
 * Reference implementation provided in TileEmcHandler
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
	long provideEMC(@Nonnull EnumFacing side, long toExtract);

	/**
	 * Extract, at most, the given amount of EMC from the given side
	 * @param side The side to extract EMC from
	 * @param toExtract The maximum amount to extract
	 * @return The amount actually extracted
	 * @deprecated Since ProjectE API version 1.2.0
	 */
	@Deprecated
	default double provideEMC(@Nonnull EnumFacing side, double toExtract) {
		return provideEMC(side, (long) toExtract);
	}
}
