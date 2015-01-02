package moze_intel.projecte.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Used by items that have a special function
 */
public interface IExtraFunction 
{
	//Called when the player presses the 'G' button
	public void doExtraFunction(ItemStack stack, EntityPlayer player);
}
