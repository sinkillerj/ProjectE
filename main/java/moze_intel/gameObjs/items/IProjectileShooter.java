package moze_intel.gameObjs.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IProjectileShooter 
{
	public boolean shootProjectile(EntityPlayer player, ItemStack stack);
}
