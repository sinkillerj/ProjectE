package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * This interface specifies items that perform a specific function when the Extra Function key is activated (default C)
 */
public interface IExtraFunction 
{
	/**
	 * Called serverside when the server receives a Extra Function key packet
	 * @param stack The ItemStack performing this function
	 * @param player The player performing this function
	 */
	void doExtraFunction(ItemStack stack, EntityPlayer player);
}
