package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.handlers.TileEntityHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

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
		sender.addChatMessage(new ChatComponentText("[ProjectE] Reloading EMC registrations..."));
			
		EMCMapper.clearMaps();
		CustomEMCParser.readUserData();
		EMCMapper.map();
		TileEntityHandler.checkAllCondensers(sender.getEntityWorld());
		
		sender.addChatMessage(new ChatComponentText("[ProjectE] Done! Sending updates to clients."));
		PacketHandler.sendFragmentedEmcPacketToAll();
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}
}
