package moze_intel.projecte.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Used by items that have different modes
 */
public interface IModeChanger 
{
	/**
	 * Returns the ItemStack's current mode
	 * @param stack
	 * @return
	 */
	public byte getMode(ItemStack stack);

	/**
	 * Called when the player presses the 'M' buton
	 * @param player
	 * @param stack
	 */
	public void changeMode(EntityPlayer player, ItemStack stack);
}
