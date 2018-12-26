package moze_intel.projecte.impl;

import moze_intel.projecte.api.item.IModeChanger;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MultiModeString implements IModeChanger {
    private static final String TAG_MODE = "Mode";
    private final ItemStack stack;
    private final String[] modes;

    public MultiModeString(ItemStack stack, String[] modes) {
        this.stack = stack;
        this.modes = modes;
    }

    @Override
    public byte getMode() {
        return stack.hasTagCompound() ? stack.getTagCompound().getByte(TAG_MODE) : 0;
    }

    @Override
    public boolean changeMode(@Nonnull EntityPlayer player, @Nullable EnumHand hand) {
        if (modes.length == 0)
        {
            return false;
        }

        byte newMode = (byte) ((getMode() + 1) % modes.length);
        ItemHelper.getOrCreateCompound(stack).setByte(TAG_MODE, newMode);

        TextComponentTranslation modeName = new TextComponentTranslation(modes[newMode]);
        player.sendMessage(new TextComponentTranslation("pe.item.mode_switch", modeName));
        return true;
    }
}
