package moze_intel.projecte.network.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

public abstract class ProjectEBaseCMD extends CommandBase
{
	@Nonnull
	@Override
	public abstract String getCommandName();
	
	@Override
	public abstract int getRequiredPermissionLevel();

	@Nonnull
	@Override
	public abstract String getCommandUsage(@Nonnull ICommandSender sender);

	@Override
	public abstract void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException;
	
	protected void sendSuccess(ICommandSender sender, ITextComponent message)
	{
		sendMessage(sender, message.setStyle(new Style().setParentStyle(message.getStyle()).setColor(TextFormatting.GREEN)));
	}
	
	protected void sendError(ICommandSender sender, ITextComponent message)
	{
		sendMessage(sender, message.setStyle(new Style().setParentStyle(message.getStyle()).setColor(TextFormatting.RED)));
	}
	
	protected void sendMessage(ICommandSender sender, ITextComponent message)
	{
		sender.addChatMessage(message);
	}
}
