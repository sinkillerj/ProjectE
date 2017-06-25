package moze_intel.projecte.network.commands;

import com.google.common.collect.Lists;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ChangelogCMD extends ProjectEBaseCMD
{
	public static final List<String> changelog = new ArrayList<>();
	
	@Nonnull
	@Override
	public String getName()
	{
		return "projecte_changelog";
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender)
	{
		return "/projecte changelog";
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params)
	{
		if (ChangelogCMD.changelog.isEmpty())
		{
			sender.sendMessage(new TextComponentTranslation("pe.command.changelog.uptodate"));
		}
		else
		{
			for (String s: ChangelogCMD.changelog)
			{
				sender.sendMessage(new TextComponentString(s));
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
