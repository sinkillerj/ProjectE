package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Used by items that trigger with the extra function key (C by default)
 */
public interface IExtraFunction 
{
	/**
	 * Do our extra function. Called serverside only.
	 * @param stack The ItemStack the player is pressing C on
	 * @param player The player
	 */
	void doExtraFunction(ItemStack stack, EntityPlayer player);
}
