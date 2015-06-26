package moze_intel.projecte.api.item;

import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import net.minecraft.item.ItemStack;

/**
 * This interface specifies items that perform a specific function every tick when inside an Alchemical Chest
 */
public interface IAlchChestItem
{
    /**
     * Called on both client and server every time the alchemical chest ticks this item.
     * Implementers that modify the chest inventory (serverside) MUST call markDirty() on the tile entity. 
     * If you do not, your changes may not be saved when the world/chunk unloads!
     *
     * @param tile The Tile being ticked
     * @param stack The ItemStack being ticked
     */
    void updateInAlchChest(AlchChestTile tile, ItemStack stack);
}
