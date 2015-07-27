package mapeper.projecte.neirecipecollector;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtils
{
	private static void addChatError(ICommandSender sender, String s) {
		ChatComponentText text = new ChatComponentText(s);
		text.getChatStyle().setColor(EnumChatFormatting.RED);
		sender.addChatMessage(text);
	}

	static void addChatError(ICommandSender sender, String format, Object... args) {
		addChatError(sender, String.format(format, args));
	}

	private static void addChatMessage(ICommandSender sender, String s) {
		sender.addChatMessage(new ChatComponentText(s));
	}

	static void addChatMessage(ICommandSender sender, String format, Object... args) {
		addChatMessage(sender, String.format(format, args));
	}
}
