package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Used by items that fire entities
 */
public interface IProjectileShooter 
{
	/**
	 * Called serverside when the player presses the Fire Projectile Button (R by default).
	 * @param player The player pressing the key
	 * @param stack The stack we are using to shoot
	 * @return If the projectile was actually fired
	 */
	boolean shootProjectile(EntityPlayer player, ItemStack stack);
}
