package moze_intel.projecte.api;

import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import net.minecraft.item.ItemStack;

/**
 * This interface specifies items that perform a specific function every tick when inside an Alchemical Chest
 */
public interface IAlchChestItem
{
    /**
     * Called on both client and server every time the alchemical chest ticks this item.
     * Implementers that modify the chest inventory MUST call markDirty() on the tile entity, or else your changes may not be saved!
     *
     * @param tile The Tile being ticked
     * @param stack The ItemStack being ticked
     */
    void updateInAlchChest(AlchChestTile tile, ItemStack stack);
}
