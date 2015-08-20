package moze_intel.projecte.network.commands;

import moze_intel.projecte.utils.LowerCasePrefixPredicate;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ProjectECMD extends ProjectEBaseCMD
{
	private static final List<String> commands = Lists.newArrayList("changelog", "clearKnowledge", "setEMC", "reloadEMC", "removeEMC", "resetEMC");

	ChangelogCMD changelogcmd = new ChangelogCMD();
	ReloadEmcCMD reloademccmd = new ReloadEmcCMD();
	SetEmcCMD setemccmd = new SetEmcCMD();
	RemoveEmcCMD removeemccmd = new RemoveEmcCMD();
	ResetEmcCMD resetemccmd = new ResetEmcCMD();
	ClearKnowledgeCMD clearknowledgecmd = new ClearKnowledgeCMD();

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
			return LowerCasePrefixPredicate.autocompletionOptions(commands, params[0]);
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

		String subName = params[0].toLowerCase(Locale.ROOT);

		if ("setemc".equals(subName))
		{
			if (setemccmd.canCommandSenderUseCommand(sender))
			{
				setemccmd.processCommand(sender, relayparams);
			}
			else
			{
				sendError(sender, new ChatComponentTranslation("commands.generic.permission"));
			}
		}
		else if ("resetemc".equals(subName))
		{
			if (resetemccmd.canCommandSenderUseCommand(sender))
			{
				resetemccmd.processCommand(sender, relayparams);
			}
			else
			{
				sendError(sender, new ChatComponentTranslation("commands.generic.permission"));
			}
		}
		else if ("removeemc".equals(subName))
		{
			if (removeemccmd.canCommandSenderUseCommand(sender))
			{
				removeemccmd.processCommand(sender, relayparams);
			}
			else
			{
				sendError(sender, new ChatComponentTranslation("commands.generic.permission"));
			}
		}
		else if ("reloademc".equals(subName))
		{
			if (reloademccmd.canCommandSenderUseCommand(sender))
			{
				reloademccmd.processCommand(sender, relayparams);
			}
			else
			{
				sendError(sender, new ChatComponentTranslation("commands.generic.permission"));
			}
		}
		else if ("clearknowledge".equals(subName))
		{
			if (clearknowledgecmd.canCommandSenderUseCommand(sender))
			{
				clearknowledgecmd.processCommand(sender, relayparams);
			}
			else
			{
				sendError(sender, new ChatComponentTranslation("commands.generic.permission"));
			}
		}
		else if ("changelog".equals(subName))
		{
			if (changelogcmd.canCommandSenderUseCommand(sender))
			{
				changelogcmd.processCommand(sender, relayparams);
			}
			else
			{
				sendError(sender, new ChatComponentTranslation("commands.generic.permission"));
			}
		}

	}
}
