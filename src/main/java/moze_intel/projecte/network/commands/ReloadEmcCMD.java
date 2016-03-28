package moze_intel.projecte.network.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.handlers.TileEntityHandler;

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
		return "/projecte reloadEMC";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] params)
	{
		sender.addChatMessage(new TextComponentTranslation("pe.command.reload.started"));

		EMCMapper.clearMaps();
		CustomEMCParser.readUserData();
		EMCMapper.map();
		TileEntityHandler.checkAllCondensers();

		sender.addChatMessage(new TextComponentTranslation("pe.command.reload.success"));

		PacketHandler.sendFragmentedEmcPacketToAll();
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}
}
