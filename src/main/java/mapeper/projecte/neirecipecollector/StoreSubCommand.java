package mapeper.projecte.neirecipecollector;

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
	public void processCommand(ICommandSender sender, List<String> params)
	{

	}
}
