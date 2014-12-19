package moze_intel.projecte.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Used by items that fire entities
 */
public interface IProjectileShooter 
{
	/**
	 * Called when the player presses the 'R' button.
	 * @return If the projectile was actually fired
	 */
	public boolean shootProjectile(EntityPlayer player, ItemStack stack);
}
