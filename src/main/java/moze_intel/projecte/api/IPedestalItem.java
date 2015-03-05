package moze_intel.projecte.api;

import net.minecraft.world.World;

/**
 * Used by items that provide special functionality in an activated DM pedestal.
 */
public interface IPedestalItem {

	/***
	 * Called on both client and server each time an active DMPedestalTile ticks with this item inside.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
    public void updateInPedestal(World world, int x, int y, int z);

}
