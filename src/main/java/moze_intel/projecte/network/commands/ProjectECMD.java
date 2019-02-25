package moze_intel.projecte.network.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nonnull;

public class ProjectECMD extends CommandTreeBase
{
	public ProjectECMD()
	{
		addSubcommand(new ClearKnowledgeCMD());
		addSubcommand(new ReloadEmcCMD());
		addSubcommand(new RemoveEmcCMD());
		addSubcommand(new ResetEmcCMD());
		addSubcommand(new SetEmcCMD());
		addSubcommand(new ShowBagCMD());
	}

	@Nonnull
	@Override
	public String getName()
	{
		return "projecte";
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender)
	{
		return "pe.command.main.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 0;
	}
}
