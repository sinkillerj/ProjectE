package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * This interface specifies items that perform a specific function when the Extra Function key is activated (default C)
 */
public interface IExtraFunction 
{
	/**
	 * Called serverside when the server receives a Extra Function key packet
	 * @param stack The ItemStack performing this function
	 * @param player The player performing this function
	 * @param hand The hand this stack was in, or null if the call was not from the player's hands
	 * @return Whether the operation succeeded
	 */
	boolean doExtraFunction(ItemStack stack, EntityPlayer player, EnumHand hand);
}
