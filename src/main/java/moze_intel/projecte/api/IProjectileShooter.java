package moze_intel.projecte.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Used by items that fire entities
 */
public interface IProjectileShooter
{
	/**
	 * Called when the {@link EntityPlayer} presses the 'R' button.
	 *
	 * @return true if the projectile was actually fired
	 */
	public boolean shootProjectile(EntityPlayer player, ItemStack stack);
}
