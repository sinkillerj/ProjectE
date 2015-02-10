package moze_intel.projecte.api;

import net.minecraft.world.World;

/**
 * Used by items that provide special functionality in a DM pedestal.
 */
public interface IPedestalItem {

    // Called every time the DM pedestal ticks.
    public void updateInPedestal(World world, int x, int y, int z);

}
