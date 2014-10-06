package moze_intel.network.commands;

import moze_intel.EMC.EMCMapper;
import moze_intel.config.FileHelper;
import moze_intel.network.PacketHandler;
import moze_intel.network.packets.ClientSyncPKT;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class ReloadCfgCMD extends ProjectEBaseCMD
{
	@Override
	public String getCommandName() 
	{
		return "projecte_reloadCFG";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/projecte_reloadCFG";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		sender.addChatMessage(new ChatComponentText("[ProjectE] Reloading EMC registrations..."));
			
		EMCMapper.clearMaps();
		FileHelper.readUserData();
		EMCMapper.map();
		
		sender.addChatMessage(new ChatComponentText("[ProjectE] Done! Sending updates to clients."));
		PacketHandler.sendToAll(new ClientSyncPKT());
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}
}
