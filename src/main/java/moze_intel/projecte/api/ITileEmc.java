package moze_intel.projecte.api;

/**
 * Interface for TileEntities with an EMC buffer.<br>
 * Note that the EMC should be a double.<br>
 * Changes to the EMC buffer are usually done server-side, so packet handling is required.
 */
public interface ITileEmc 
{
	/**
	 * Set the EMC value of the tile entity.<br>
	 * Only called on the server-side.
	 * @param value
	 */
	public void setEmc(double value);

	/**
	 * Add EMC to the tile entity.<br>
	 * Only called on the server-side.
	 * @param value
	 */
	public void addEmc(double value);

	/**
	 * Remove EMC from the tile entity.<br>
	 * Only called on the server-side.
	 * @param value
	 */
	public void removeEmc(double value);

	/**
	 * @return The amount of EMC stored in the tile entity.
	 */
	public double getStoredEmc();

	/**
	 * @return Whether the EMC buffer is full or not.
	 */
	public boolean hasMaxedEmc();
	
	/**
	 * Returns whether the tile is requesting EMC.<br>
	 * If this returns true, it will accept EMC from any valid provider.<br>
	 * EMC will be received only on the server side. 
	 */
	public boolean isRequestingEmc();
}
