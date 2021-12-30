package moze_intel.projecte.api.capabilities.item;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;

/**
 * This interface specifies items that perform a specific function every tick when inside an Alchemical Chest
 *
 * This is exposed through the Capability system.
 *
 * Acquire an instance of this using {@link ItemStack#getCapability(Capability, net.minecraft.core.Direction)}.
 *
 * @author williewillus
 */
public interface IAlchChestItem {

	/**
	 * Called on both client and server every time the alchemical chest ticks this item Implementers that modify the chest inventory (serverside) MUST call markDirty() on
	 * the tile entity. If you do not, your changes may not be saved when the world/chunk unloads!
	 *
	 * @param world The World
	 * @param stack The ItemStack being ticked
	 *
	 * @return {@code true} if the passed in stack had its NBT modified to ensure it can be saved.
	 */
	boolean updateInAlchChest(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull ItemStack stack);
}