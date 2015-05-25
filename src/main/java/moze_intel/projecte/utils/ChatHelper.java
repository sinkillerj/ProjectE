package moze_intel.projecte.utils;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * Helper class for chat messages and formatting
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class ChatHelper
{
	/**
	 * Alters color of a IChatComponent and returns it.
	 * Returning the param allows for a chat message to be constructed and colored in one line.
	 */
	public static IChatComponent modifyColor(IChatComponent chat, EnumChatFormatting format)
	{
		if (format.isColor())
		{
			chat.getChatStyle().setColor(format);
		}
		return chat;
	}
}
