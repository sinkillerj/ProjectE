package moze_intel.projecte.network.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public abstract class ProjectEBaseCMD extends CommandBase
{
	@Override
	public abstract String getCommandName();
	
	@Override
	public abstract int getRequiredPermissionLevel();

	@Override
	public abstract String getCommandUsage(ICommandSender sender);

	@Override
	public abstract void processCommand(ICommandSender sender, String[] params);
	
	protected void sendSuccess(ICommandSender sender, String message)
	{
		sendMessage(sender, EnumChatFormatting.GREEN + message);
	}
	
	protected void sendError(ICommandSender sender, String message)
	{
		sendMessage(sender, EnumChatFormatting.RED + message);
	}
	
	protected void sendMessage(ICommandSender sender, String message)
	{
		sender.addChatMessage(new ChatComponentText(message));
	}

	protected int parseInteger(String string)
	{
		int value;

		try
		{
			value = Integer.parseInt(string);
		}
		catch (NumberFormatException e)
		{
			return -1;
		}

		return value;
	}
}
