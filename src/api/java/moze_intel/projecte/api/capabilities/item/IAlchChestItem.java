package moze_intel.projecte.api.capabilities.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.NotNull;

/**
 * This interface specifies items that perform a specific function every tick when inside an Alchemical Chest
 * <p>
 * This is exposed through the Capability system.
 * <p>
 * Acquire an instance of this using {@link ItemStack#getCapability(ItemCapability)}.
 *
 * @author williewillus
 */
public interface IAlchChestItem {

	/**
	 * Called on both client and server every time the alchemical chest ticks this item Implementers that modify the chest inventory (serverside) MUST call markDirty() on
	 * the block entity. If you do not, your changes may not be saved when the world/chunk unloads!
	 *
	 * @param level The Level
	 * @param stack The ItemStack being ticked
	 *
	 * @return {@code true} if the passed in stack had its NBT modified to ensure it can be saved.
	 */
	boolean updateInAlchChest(@NotNull Level level, @NotNull BlockPos pos, @NotNull ItemStack stack);
}