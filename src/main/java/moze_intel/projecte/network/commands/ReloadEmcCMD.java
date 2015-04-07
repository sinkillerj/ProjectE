package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.ThreadReloadEMCMap;
import moze_intel.projecte.handlers.TileEntityHandler;
import moze_intel.projecte.network.PacketHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

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
		sender.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("pe.command.reload.started")));
		ThreadReloadEMCMap.runEMCRemap(false, sender.getEntityWorld());
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}
}
