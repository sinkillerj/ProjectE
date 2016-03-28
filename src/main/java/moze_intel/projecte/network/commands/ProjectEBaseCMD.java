package moze_intel.projecte.network.commands;

import moze_intel.projecte.utils.ChatHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.ITextComponent;

public abstract class ProjectEBaseCMD extends CommandBase
{
	@Override
	public abstract String getCommandName();
	
	@Override
	public abstract int getRequiredPermissionLevel();

	@Override
	public abstract String getCommandUsage(ICommandSender sender);

	@Override
	public abstract void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException;
	
	protected void sendSuccess(ICommandSender sender, ITextComponent message)
	{
		sendMessage(sender, ChatHelper.modifyColor(message, TextFormatting.GREEN));
	}
	
	protected void sendError(ICommandSender sender, ITextComponent message)
	{
		sendMessage(sender, ChatHelper.modifyColor(message, TextFormatting.RED));
	}
	
	protected void sendMessage(ICommandSender sender, ITextComponent message)
	{
		sender.addChatMessage(message);
	}
}
