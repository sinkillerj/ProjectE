package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface IItemMode extends IModeChanger {

	ILangEntry[] getModeLangEntries();

	default byte getModeCount() {
		return (byte) getModeLangEntries().length;
	}

	default ILangEntry getModeLangEntry(ItemStack stack) {
		ILangEntry[] langEntries = getModeLangEntries();
		byte mode = getMode(stack);
		if (mode < 0 || mode >= langEntries.length) {
			return PELang.INVALID_MODE;
		}
		return langEntries[mode];
	}

	@Override
	default byte getMode(@NotNull ItemStack stack) {
		return stack.hasTag() ? stack.getOrCreateTag().getByte(Constants.NBT_KEY_MODE) : 0;
	}

	@Override
	default boolean changeMode(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
		byte numModes = getModeCount();
		if (numModes < 2) {
			//If we have no modes or we are set to the only mode fail
			return false;
		}
		//Update the mode
		stack.getOrCreateTag().putByte(Constants.NBT_KEY_MODE, (byte) ((getMode(stack) + 1) % numModes));
		player.sendSystemMessage(getModeSwitchEntry().translate(getModeLangEntry(stack)));
		return true;
	}

	default ILangEntry getModeSwitchEntry() {
		return PELang.MODE_SWITCH;
	}

	default Component getToolTip(ItemStack stack) {
		return PELang.CURRENT_MODE.translate(ChatFormatting.AQUA, getModeLangEntry(stack));
	}
}