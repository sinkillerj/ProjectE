package moze_intel.projecte.gameObjs.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IModeChanger 
{
	public void changeMode(EntityPlayer player, ItemStack stack);
}
