package moze_intel.projecte.api.capabilities.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraftforge.common.capabilities.Capability;

/**
 * This interface specifies items that perform a specific function when the Extra Function key is activated (default C)
 *
 * This is exposed through the Capability system.
 *
 * Acquire an instance of this using {@link ItemStack#getCapability(Capability, Direction)}.
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
	boolean doExtraFunction(@Nonnull ItemStack stack, @Nonnull PlayerEntity player, @Nullable Hand hand);
}