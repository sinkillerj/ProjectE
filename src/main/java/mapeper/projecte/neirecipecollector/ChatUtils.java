package mapeper.projecte.neirecipecollector;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtils
{
	public static void addChatError(ICommandSender sender, String s) {
		ChatComponentText text = new ChatComponentText(s);
		text.getChatStyle().setColor(EnumChatFormatting.RED);
		sender.addChatMessage(text);
	}

	public static void addChatError(ICommandSender sender, String format, Object... args) {
		addChatError(sender, String.format(format, args));
	}

	public static void addChatMessage(ICommandSender sender, String s) {
		sender.addChatMessage(new ChatComponentText(s));
	}

	public static void addChatMessage(ICommandSender sender, String format, Object... args) {
		addChatMessage(sender, String.format(format, args));
	}
}
