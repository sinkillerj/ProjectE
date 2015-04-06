package moze_intel.projecte.network.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

public class ChangelogCMD extends ProjectEBaseCMD
{
	public static final List<String> changelog = new ArrayList<>();
	
	@Override
	public String getCommandName() 
	{
		return "projecte_log";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/projecte_log";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		if (ChangelogCMD.changelog.isEmpty())
		{
			sender.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("pe.command.changelog.uptodate")));
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
