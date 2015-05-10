package moze_intel.projecte.utils;

import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

/**
 * Utility class to get comparator outputs for a block
 */
public final class ComparatorCalculators
{
    public static int getForAlchChest(World world, int x, int y, int z)
    {
        return Container.calcRedstoneFromInventory(((AlchChestTile) world.getTileEntity(x, y, z)));
    }

    public static int getForCondenser(World world, int x, int y, int z)
    {
        return Container.calcRedstoneFromInventory(((CondenserTile) world.getTileEntity(x, y, z)));
    }

    public static int getForMatterFurnace(World world, int x, int y, int z)
    {
        return Container.calcRedstoneFromInventory(((RMFurnaceTile) world.getTileEntity(x, y, z)));
    }
}
