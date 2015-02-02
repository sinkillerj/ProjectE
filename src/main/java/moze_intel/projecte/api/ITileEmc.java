package moze_intel.projecte.api;

/**
 * Interface for TileEntities with an EMC buffer.<br>
 * Changes to the EMC buffer are usually done server-side, so packet handling is required.
 */
public interface ITileEmc
{

	/**
	 * Set the EMC value of the tile entity.<br>
	 * Gets called only server-side. 
	 *
	 * @param value The EMC value
	 */
	public void setEmc(int value);
	
	/**
	 * Add EMC to the tile entity.<br>
	 * Gets called only server-side. 
	 * 
	 * @param value The EMC value 
	 */
	public void addEmc(int value);
	
	/**
	 * Remove EMC from the tile entity.<br>
	 * Gets called only server-side. 
	 * 
	 * @param value The EMC value 
	 */
	public void removeEmc(int value);

	/**
	 * Should return the amount of stored EMC.
	 *
	 * @return the amount of stored EMC.
	 */
	public int getStoredEmc();
	
	/**
	 * Returns whether or not the EMC buffer is full.  
	 * 
	 * @return true if the EMC buffer is full.
	 */
	public boolean hasMaxedEmc();
	
	/**
	 * Returns whether the tile is requesting EMC.<br>
	 * If this returns true, it will accept EMC from any valid provider.<br>
	 * EMC will be received only on the server side. 
	 * 
	 * @return true if the tile is requesting EMC.
	 */
	public boolean isRequestingEmc();
}
