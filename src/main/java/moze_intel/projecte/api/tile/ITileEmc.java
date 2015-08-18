package moze_intel.projecte.api.tile;

/**
 * This interface is now DEPRECATED. Do not use! Update to IEMCAcceptor, IEMCProvider, or TileEmcHandler ASAP!!
 * You will not crash, but using this will have no effect as the ProjectE machines no longer recognize nor implement this interface
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
