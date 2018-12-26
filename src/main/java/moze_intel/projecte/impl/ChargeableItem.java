package moze_intel.projecte.impl;

import moze_intel.projecte.api.PESounds;
import moze_intel.projecte.api.item.IItemCharge;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChargeableItem implements IItemCharge
{
    private static final String KEY = "Charge";

    private final ItemStack stack;
    private final int numCharges;

    public ChargeableItem(ItemStack stack, int numCharges) {
        this.stack = stack;
        this.numCharges = numCharges;
    }

    @Override
    public int getNumCharges() {
        return numCharges;
    }

    @Override
    public int getCharge() {
        if (!stack.hasTagCompound())
        {
            return 0;
        }

        return stack.getTagCompound().getInteger(KEY);
    }

    @Override
    public boolean changeCharge(@Nonnull EntityPlayer player, @Nullable EnumHand hand) {
        int currentCharge = getCharge();
        int numCharges = getNumCharges();

        if (player.isSneaking())
        {
            if (currentCharge > 0)
            {
                player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.UNCHARGE, SoundCategory.PLAYERS, 1.0F, 0.5F + ((0.5F / (float)numCharges) * currentCharge));
                ItemHelper.getOrCreateCompound(stack).setInteger(KEY, currentCharge - 1);
                return true;
            }
        }
        else if (currentCharge < numCharges)
        {
            player.getEntityWorld().playSound(null, player.posX, player.posY, player.posZ, PESounds.CHARGE, SoundCategory.PLAYERS, 1.0F, 0.5F + ((0.5F / (float)numCharges) * currentCharge));
            ItemHelper.getOrCreateCompound(stack).setInteger(KEY, currentCharge + 1);
            return true;
        }

        return false;
    }
}
