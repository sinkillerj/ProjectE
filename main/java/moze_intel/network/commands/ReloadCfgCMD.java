package moze_intel.network.commands;

import moze_intel.MozeCore;
import moze_intel.EMC.EMCMapper;
import moze_intel.config.FileHelper;
import moze_intel.network.packets.ClientSyncPKT;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ReloadCfgCMD extends ProjectEBaseCMD
{
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/projecte reloadcfg";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		if (params[0].equals("reloadcfg"))
		{
			sender.addChatMessage(new ChatComponentText("[ProjectE] Reloading EMC registrations..."));
			
			EMCMapper.clearMap();
			FileHelper.readUserData();
			EMCMapper.map();
			
			sender.addChatMessage(new ChatComponentText("[ProjectE] Done! Sending updates to clients."));
			MozeCore.pktHandler.sendToAll(new ClientSyncPKT());
		}
	}
}
