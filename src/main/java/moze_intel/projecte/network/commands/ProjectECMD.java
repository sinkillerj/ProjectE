package moze_intel.projecte.network.commands;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

import java.util.Arrays;
import java.util.List;

public class ProjectECMD extends ProjectEBaseCMD
{
	private static final String changelogCmdName = "changelog";
	private static final String clearKnowledgeCmdName = "clearKnowledge";
	private static final String setEmcCmdName = "setEMC";
	private static final String reloadEmcCmdName = "reloadEMC";
	private static final String removeEmcCmdName = "removeEMC";
	private static final String resetEmcCmdName = "resetEMC";
	private static final List<String> commands = Lists.newArrayList(changelogCmdName, clearKnowledgeCmdName, setEmcCmdName, reloadEmcCmdName, removeEmcCmdName, resetEmcCmdName);

	ChangelogCMD changelogcmd = new ChangelogCMD();
	ReloadEmcCMD reloademccmd = new ReloadEmcCMD();
	SetEmcCMD setemccmd = new SetEmcCMD();
	RemoveEmcCMD removeemccmd = new RemoveEmcCMD();
	ResetEmcCMD resetemccmd = new ResetEmcCMD();
	ClearKnowledgeCMD clearknowledgecmd = new ClearKnowledgeCMD();
	ImmutableMap<String,ProjectEBaseCMD> commandMap;
	public ProjectECMD()
	{
		ImmutableMap.Builder<String, ProjectEBaseCMD> builder = ImmutableMap.builder();

		//Commands as lowercase
		builder.put(changelogCmdName.toLowerCase(), changelogcmd);
		builder.put(clearKnowledgeCmdName.toLowerCase(), clearknowledgecmd);
		builder.put(setEmcCmdName.toLowerCase(), setemccmd);
		builder.put(reloadEmcCmdName.toLowerCase(), reloademccmd);
		builder.put(removeEmcCmdName.toLowerCase(), removeemccmd);
		builder.put(resetEmcCmdName.toLowerCase(), resetemccmd);
		commandMap = builder.build();
	}

	@Override
	public String getCommandName() 
	{
		return "projecte";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "pe.command.main.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 0;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] params)
	{
		if (params.length == 1)
		{
			return Lists.newArrayList(Iterables.filter(commands, new LowerCasePrefixPredicate(params[0])));
		}

		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		if (params.length < 1)
		{
			sendError(sender, new ChatComponentTranslation("pe.command.main.usage"));
			return;
		}

		String[] relayparams = new String[0];

		if (params.length > 1)
		{
			relayparams = Arrays.copyOfRange(params, 1, params.length);
		}

		String commandName = params[0].toLowerCase();

		if (commandMap.containsKey(commandName)) {
			ProjectEBaseCMD command = commandMap.get(commandName);
			if (command.canCommandSenderUseCommand(sender))
			{
				command.processCommand(sender, relayparams);
			}
			else
			{
				sendError(sender, new ChatComponentTranslation("commands.generic.permission"));
			}
		}
		else
		{
			sendError(sender, new ChatComponentTranslation("pe.command.main.usage"));
			return;
		}
	}

	private static class LowerCasePrefixPredicate implements Predicate<String>
	{
		private final String prefix;
		public LowerCasePrefixPredicate(String prefix)
		{
			this.prefix = prefix;
		}

		@Override
		public boolean apply(String input)
		{
			return input.toLowerCase().startsWith(prefix.toLowerCase());
		}
	}
}
