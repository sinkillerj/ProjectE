package moze_intel.projecte.api.tile;

/**
 * Defines the contract for arbitrary objects that can store EMC
 * You usually do not want to use this directly
 * Use extensions IEMCAcceptor and IEMCProvider, or the provided reference implementations instead
 *
 * @author williewillus
 */
public interface IEmcStorage
{
	/**
	 * Gets the current amount of EMC in this IEMCStorage
	 * @return The current EMC stored
	 */
	long getStoredEmc();

	/**
	 * Gets the maximum amount of EMC this IEMCStorage is allowed to contain
	 * @return The maximum EMC allowed
	 */
	long getMaximumEmc();
}
