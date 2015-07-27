package mapeper.projecte.neirecipecollector;

import net.minecraft.command.ICommandSender;

import java.util.List;

public interface ISubCommand
{
	public String getCommandName();
	public List<String> addTabCompletionOptions(List<String> params);
	public void processCommand(String previousCommands, ICommandSender sender, List<String> params);
}
