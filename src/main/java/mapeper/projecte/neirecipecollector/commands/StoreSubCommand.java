package mapeper.projecte.neirecipecollector.commands;

import net.minecraft.command.ICommandSender;

import java.util.List;

public class StoreSubCommand implements ISubCommand
{
	@Override
	public String getCommandName()
	{
		return "store";
	}

	@Override
	public List<String> addTabCompletionOptions(List<String> params)
	{
		return null;
	}

	@Override
	public void processCommand(String previousCommands, ICommandSender sender, List<String> params)
	{

	}
}
