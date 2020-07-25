package moze_intel.projecte.gameObjs.items;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.utils.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public interface IItemMode extends IModeChanger {

	String[] getModeTranslationKeys();

	default byte getModeCount() {
		return (byte) getModeTranslationKeys().length;
	}

	default String getModeTranslationKey(ItemStack stack) {
		String[] translationKeys = getModeTranslationKeys();
		byte mode = getMode(stack);
		if (mode < 0 || mode >= translationKeys.length) {
			return "pe.item.mode.invalid";
		}
		return translationKeys[mode];
	}

	@Override
	default byte getMode(@Nonnull ItemStack stack) {
		return stack.hasTag() ? stack.getTag().getByte(Constants.NBT_KEY_MODE) : 0;
	}

	@Override
	default boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		byte numModes = getModeCount();
		if (numModes < 2) {
			//If we have no modes or we are set to the only mode fail
			return false;
		}
		//Update the mode
		stack.getOrCreateTag().putByte(Constants.NBT_KEY_MODE, (byte) ((getMode(stack) + 1) % numModes));
		TranslationTextComponent modeName = new TranslationTextComponent(getModeTranslationKey(stack));
		player.sendMessage(new TranslationTextComponent("pe.item.mode_switch", modeName), Util.DUMMY_UUID);
		return true;
	}

	default ITextComponent getToolTip(ItemStack stack) {
		return new TranslationTextComponent("pe.item.mode").appendString(": ")
				.append(new TranslationTextComponent(getModeTranslationKey(stack)).mergeStyle(TextFormatting.AQUA));
	}
}