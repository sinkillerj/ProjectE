package moze_intel.projecte.network.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.handlers.TileEntityHandler;

import javax.annotation.Nonnull;

public class ReloadEmcCMD extends ProjectEBaseCMD
{
	@Nonnull
	@Override
	public String getCommandName() 
	{
		return "projecte_reloadEMC";
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(@Nonnull ICommandSender sender)
	{
		return "/projecte reloadEMC";
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params)
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
