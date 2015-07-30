package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * This interface specifies items that fire a projectile when the Shoot Projectile keybind is activated (default R)
 */
public interface IProjectileShooter 
{
	/**
	 * Called serverside when the player presses the Fire Projectile Button
	 * @param player The player pressing the key
	 * @param stack The stack we are using to shoot
	 * @return If the projectile was actually fired
	 */
	boolean shootProjectile(EntityPlayer player, ItemStack stack);
}
