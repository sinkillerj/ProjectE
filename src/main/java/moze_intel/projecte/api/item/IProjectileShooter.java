package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interface specifies items that fire a projectile when the Shoot Projectile keybind is activated (default R)
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
	boolean shootProjectile(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nullable EnumHand hand);
}
