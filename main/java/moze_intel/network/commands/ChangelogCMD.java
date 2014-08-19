package moze_intel.network.commands;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ChangelogCMD extends ProjectEBaseCMD
{
	public static final List<String> changelog = new ArrayList();

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/projecte changelog";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		if (params.length == 0)
		{
			return;
		}
		
		if (params[0].equalsIgnoreCase("changelog"))
		{
			if (ChangelogCMD.changelog.isEmpty())
			{
				sender.addChatMessage(new ChatComponentText("ProjectE is up to date."));
			}
			else
			{
				for (String s: ChangelogCMD.changelog)
				{
					sender.addChatMessage(new ChatComponentText(s));
				}
			}
		}
	}
}
