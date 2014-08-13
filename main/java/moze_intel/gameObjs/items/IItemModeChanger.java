package moze_intel.gameObjs.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IItemModeChanger 
{
	public void changeMode(EntityPlayer player, ItemStack stack);
}
