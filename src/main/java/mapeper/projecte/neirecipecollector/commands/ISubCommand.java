package mapeper.projecte.neirecipecollector.commands;

import net.minecraft.command.ICommandSender;

import java.util.List;

public interface ISubCommand
{
	public String getCommandName();
	public List<String> addTabCompletionOptions(List<String> params);
	public void processCommand(String previousCommands, ICommandSender sender, List<String> params);
}
