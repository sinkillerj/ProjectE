package moze_intel.network.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

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
}
