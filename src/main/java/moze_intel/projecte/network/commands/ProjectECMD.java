package moze_intel.projecte.network.commands;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ProjectECMD extends ProjectEBaseCMD
{
	private static final List<String> commands = Lists.newArrayList("changelog", "clearKnowledge", "setEMC", "reloadEMC", "removeEMC", "resetEMC");

	private final ChangelogCMD changelogcmd = new ChangelogCMD();
	private final ReloadEmcCMD reloademccmd = new ReloadEmcCMD();
	private final SetEmcCMD setemccmd = new SetEmcCMD();
	private final RemoveEmcCMD removeemccmd = new RemoveEmcCMD();
	private final ResetEmcCMD resetemccmd = new ResetEmcCMD();
	private final ClearKnowledgeCMD clearknowledgecmd = new ClearKnowledgeCMD();

	@Nonnull
	@Override
	public String getCommandName() 
	{
		return "projecte";
	}

	@Nonnull
	@Override
	public String getCommandUsage(@Nonnull ICommandSender sender)
	{
		return "pe.command.main.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 0;
	}

	@Nonnull
	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] params, BlockPos pos)
	{
		if (params.length == 1)
		{
			return Lists.newArrayList(Iterables.filter(commands, new LowerCasePrefixPredicate(params[0])));
		}

		return ImmutableList.of();
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException
	{
		if (params.length < 1)
		{
			sendError(sender, new TextComponentTranslation("pe.command.main.usage"));
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
			if (setemccmd.checkPermission(server, sender))
			{
				setemccmd.execute(server, sender, relayparams);
			}
			else
			{
				sendError(sender, new TextComponentTranslation("commands.generic.permission"));
			}
		}
		else if ("resetemc".equals(subName))
		{
			if (resetemccmd.checkPermission(server, sender))
			{
				resetemccmd.execute(server, sender, relayparams);
			}
			else
			{
				sendError(sender, new TextComponentTranslation("commands.generic.permission"));
			}
		}
		else if ("removeemc".equals(subName))
		{
			if (removeemccmd.checkPermission(server, sender))
			{
				removeemccmd.execute(server, sender, relayparams);
			}
			else
			{
				sendError(sender, new TextComponentTranslation("commands.generic.permission"));
			}
		}
		else if ("reloademc".equals(subName))
		{
			if (reloademccmd.checkPermission(server, sender))
			{
				reloademccmd.execute(server, sender, relayparams);
			}
			else
			{
				sendError(sender, new TextComponentTranslation("commands.generic.permission"));
			}
		}
		else if ("clearknowledge".equals(subName))
		{
			if (clearknowledgecmd.checkPermission(server, sender))
			{
				clearknowledgecmd.execute(server, sender, relayparams);
			}
			else
			{
				sendError(sender, new TextComponentTranslation("commands.generic.permission"));
			}
		}
		else if ("changelog".equals(subName))
		{
			if (changelogcmd.checkPermission(server, sender))
			{
				changelogcmd.execute(server, sender, relayparams);
			}
			else
			{
				sendError(sender, new TextComponentTranslation("commands.generic.permission"));
			}
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
			return input.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT));
		}
	}
}
