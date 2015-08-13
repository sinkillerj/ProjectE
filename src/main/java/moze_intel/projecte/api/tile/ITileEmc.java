package moze_intel.projecte.api.tile;

/**
 * Interface for TileEntities with an EMC buffer
 * NOTE: Changes can be called on both sides, but it is up to you to handle syncing between client and server
 *
 * This interface is now DEPRECATED. Do not use! Update to IEmcAcceptor, IEmcProvider, or TileEmc ASAP!!
 */
@Deprecated
public interface ITileEmc 
{
	/**
	 * Set the EMC value of this Tile Entity
	 * @param value The EMC amount to set
	 */
	@Deprecated
	void setEmc(double value);

	/**
	 * Add EMC to this Tile Entity
	 * @param value The EMC amount to add
	 */
	@Deprecated
	void addEmc(double value);
	
	/**
	 * Remove EMC from the tile entity
	 * @param value The EMC amount to remove
	 */
	@Deprecated
	void removeEmc(double value);
	
	/**
	 * @return The stored EMC in this TileEntity
	 */
	@Deprecated
	double getStoredEmc();
	
	/**
	 * @return Whether or not the EMC buffer is full
	 */
	@Deprecated
	boolean hasMaxedEmc();
	
	/**
	 * If this returns true, the Tile Entity will accept EMC from any valid provider
	 * EMC will be received only on the server side
	 * @return If this Tile Entity can accept EMC from adjacent providers
	 */
	@Deprecated
	boolean isRequestingEmc();
}
