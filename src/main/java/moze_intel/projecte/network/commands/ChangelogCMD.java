package moze_intel.projecte.network.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import java.util.Collections;
import java.util.List;

public class ChangelogCMD extends ProjectEBaseCMD
{
	public static List<String> changelog = Collections.emptyList();
	
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
	public void processCommand(ICommandSender sender, String[] params) 
	{
		if (ChangelogCMD.changelog.isEmpty())
		{
			sender.addChatMessage(new ChatComponentTranslation("pe.command.changelog.uptodate"));
		}
		else
		{
			for (String s: ChangelogCMD.changelog)
			{
				sender.addChatMessage(new ChatComponentText(s));
			}
		}
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 0;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}
}
