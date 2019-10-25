package moze_intel.projecte.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * This interface specifies items that perform a specific function every tick when inside an Alchemical Chest
 *
 * @author williewillus
 */
public interface IAlchChestItem
{
    /**
     * Called on both client and server every time the alchemical chest ticks this item
     * Implementers that modify the chest inventory (serverside) MUST call markDirty() on the tile entity. 
     * If you do not, your changes may not be saved when the world/chunk unloads!
     *
     * @param world The World
     * @param stack The ItemStack being ticked
     */
    void updateInAlchChest(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull ItemStack stack);
}
