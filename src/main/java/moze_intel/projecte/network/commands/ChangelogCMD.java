package moze_intel.projecte.network.commands;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.List;

public class ChangelogCMD extends ProjectEBaseCMD
{
	public static final List<String> changelog = Lists.newArrayList();
	
	@Override
	public String getCommandName() 
	{
		return "projecte_changelog";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/projecte changelog";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params)
	{
		if (ChangelogCMD.changelog.isEmpty())
		{
			sender.addChatMessage(new TextComponentTranslation("pe.command.changelog.uptodate"));
		}
		else
		{
			for (String s: ChangelogCMD.changelog)
			{
				sender.addChatMessage(new TextComponentString(s));
			}
		}
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}
}
