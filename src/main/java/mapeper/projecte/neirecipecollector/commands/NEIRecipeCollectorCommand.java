package mapeper.projecte.neirecipecollector.commands;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import moze_intel.projecte.utils.LowerCasePrefixPredicate;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nullable;
import java.util.List;

public class NEIRecipeCollectorCommand extends CommandBase
{
	ImmutableList<ISubCommand> subCommands = ImmutableList.<ISubCommand>of(new CollectSubCommand(), new StoreSubCommand());
	ImmutableMap<String, ISubCommand> subCommandsPerName = Maps.uniqueIndex(subCommands, new Function<ISubCommand, String>()
	{
		@Nullable
		@Override
		public String apply(ISubCommand input)
		{
			return input.getCommandName();
		}
	});

	@Override
	public String getCommandName()
	{
		return "neircollect";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] params)
	{
		if (params.length == 1) {
			return LowerCasePrefixPredicate.autocompletionOptions(subCommandsPerName.keySet(), params[0]);
		} else if (params.length > 1) {
			ISubCommand subCommand = subCommandsPerName.get(params[0]);
			if (subCommand != null) {
				return subCommand.addTabCompletionOptions(Arrays.asList(params).subList(1, params.length));
			}
		}
		return null;
	}

	private final String usage = String.format(
			"Use '/%s collect' to read recipes from specific NEI GUIs. " +
					"You then need to store the collected mappings to a file using '/%s store'",
			this.getCommandName(),this.getCommandName());

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return usage;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params)
	{
		if (params.length == 0) {
			addChatMessage(sender, usage);
			return;
		}
		if (params.length >= 1) {
			ISubCommand subCommand = subCommandsPerName.get(params[0].toLowerCase());
			if (subCommand != null) {
				subCommand.processCommand(String.format("/%s %s", getCommandName(), params[0].toLowerCase()), sender, Arrays.asList(params).subList(1, params.length));
			} else {
				addChatMessage(sender, String.format("ERROR: Unknown command %s", params[0]));
				addChatMessage(sender, usage);
			}
		}

	}

	private void addChatMessage(ICommandSender sender, String s) {
		sender.addChatMessage(new ChatComponentText(s));
	}
}
