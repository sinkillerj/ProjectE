package moze_intel.projecte.network.commands;

import moze_intel.projecte.emc.ThreadReloadEMCMap;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

public class ReloadEmcCMD extends ProjectEBaseCMD
{
	@Override
	public String getCommandName() 
	{
		return "projecte_reloadEMC";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/projecte_reloadEMC";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		sender.addChatMessage(new ChatComponentTranslation("pe.command.reload.started"));
		ThreadReloadEMCMap.runEMCRemap(false, sender.getEntityWorld());
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}
}
