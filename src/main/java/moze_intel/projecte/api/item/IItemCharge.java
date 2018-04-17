package moze_intel.projecte.api.item;

import moze_intel.projecte.api.PESounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This interface specifies items that have a charge that changes when the respective keybinding is activated (default V)
 */
public interface IItemCharge 
{
	String KEY = "Charge";

	int getNumCharges(@Nonnull ItemStack stack);
	/**
	 * Returns the current charge on the given ItemStack
	 * @param stack Stack whose charge we want
	 * @return The charge on the stack
	 */
	default int getCharge(@Nonnull ItemStack stack) {
		if (!stack.hasTagCompound())
		{
			stack.setTagCompound(new NBTTagCompound());
		}

		return stack.getTagCompound().getInteger(KEY);
	}

	/**
	 * Called serverside when the player presses the charge keybinding; reading sneaking state is up to you
	 * @param player The player
	 * @param stack The item being charged
	 * @param hand The hand this stack was in, or null if the call was not from the player's hands
	 * @return Whether the operation succeeded
	 */
	default boolean changeCharge(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nullable EnumHand hand) {
		int currentCharge = getCharge(stack);
		int numCharges = getNumCharges(stack);

		if (player.isSneaking())
		{
			if (currentCharge > 0)
			{
				player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.UNCHARGE, SoundCategory.PLAYERS, 1.0F, 0.5F + ((0.5F / (float)numCharges) * currentCharge));
				stack.getTagCompound().setInteger(KEY, currentCharge - 1);
				return true;
			}
		}
		else if (currentCharge < numCharges)
		{
			player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0F, 0.5F + ((0.5F / (float)numCharges) * currentCharge));
			stack.getTagCompound().setInteger(KEY, currentCharge + 1);
			return true;
		}

		return false;
	}
}
