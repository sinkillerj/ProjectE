package moze_intel.projecte.api.capabilities.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface specifies items that perform a specific function when the Extra Function key is activated (default C)
 * <p>
 * This is exposed through the Capability system.
 * <p>
 * Acquire an instance of this using {@link ItemStack#getCapability(ItemCapability)}.
 */
public interface IExtraFunction {

	/**
	 * Called serverside when the server receives a Extra Function key packet
	 *
	 * @param stack  The ItemStack performing this function
	 * @param player The player performing this function
	 * @param hand   The hand this stack was in, or null if the call was not from the player's hands
	 *
	 * @return Whether the operation succeeded
	 */
	boolean doExtraFunction(@NotNull ItemStack stack, @NotNull Player player, @Nullable InteractionHand hand);
}