package moze_intel.projecte.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Used by items that have a "charge".
 */
public interface IItemCharge 
{
	//Return the charge the ItemStack currently has
	public byte getCharge(ItemStack stack);

	//Called whenever the item needs to change it's charge (the 'V' button is pressed)
	public void changeCharge(EntityPlayer player, ItemStack stack);
}
