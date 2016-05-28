package moze_intel.projecte.utils;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

/**
 * Helper class for chat messages and formatting
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class ChatHelper
{
	/**
	 * Alters color of a ITextComponent and returns it.
	 * Returning the param allows for a chat message to be constructed and colored in one line.
	 */
	public static ITextComponent modifyColor(ITextComponent chat, TextFormatting format)
	{
		if (format.isColor())
		{
			chat.getStyle().setColor(format);
		}
		return chat;
	}
}
