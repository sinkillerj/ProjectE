package moze_intel.projecte.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Used by items that have different modes
 */
public interface IModeChanger 
{
	public byte getMode(ItemStack stack);

	//Called when the player presses the 'M' buton
	public void changeMode(EntityPlayer player, ItemStack stack);
}
