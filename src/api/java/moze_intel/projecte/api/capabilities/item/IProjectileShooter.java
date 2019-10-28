package moze_intel.projecte.api.capabilities.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraftforge.common.capabilities.Capability;

/**
 * This interface specifies items that fire a projectile when the Shoot Projectile keybind is activated (default R)
 *
 * This is exposed through the Capability system.
 *
 * Acquire an instance of this using {@link ItemStack#getCapability(Capability, Direction)}.
 */
public interface IProjectileShooter 
{
	/**
	 * Called serverside when the player presses the Fire Projectile Button
	 * @param player The player pressing the key
	 * @param stack The stack we are using to shoot
	 * @param hand The hand this stack was in, or null if the call was not from the player's hands
	 * @return If the projectile was actually fired
	 */
	boolean shootProjectile(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, @Nullable Hand hand);
}
