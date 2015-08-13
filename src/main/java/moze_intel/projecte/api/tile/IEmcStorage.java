package moze_intel.projecte.api.tile;

/**
 * Defines the contract for arbitrary objects that can store EMC
 * You usually do not want to use this directly
 * Use extensions IEmcAcceptor and IEmcProvider, or the reference implementation TileEmc instead
 *
 * @author williewillus
 */
public interface IEmcStorage
{
	/**
	 * Gets the current amount of EMC in this IEmcStorage
	 * @return The current EMC stored
	 */
	double getStoredEmc();

	/**
	 * Gets the maximum amount of EMC this IEmcStorage is allowed to contain
	 * @return The maximum EMC allowed
	 */
	double getMaximumEmc();
}
