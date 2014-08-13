package moze_intel.events;

import moze_intel.MozeCore;
import moze_intel.EMC.EMCMapper;
import moze_intel.network.packets.ClientSyncPKT;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ConnectionHandler 
{
	@SubscribeEvent
	public void playerConnect(PlayerLoggedInEvent event)
	{
		System.out.println("Player "+event.player.getCommandSenderName()+" logged in.");
		MozeCore.pktHandler.sendTo(new ClientSyncPKT(EMCMapper.emc), (EntityPlayerMP) event.player);
	}
	
	@SubscribeEvent
	public void playerDisconnect(ClientDisconnectionFromServerEvent event)
	{
		String userName = Minecraft.getMinecraft().thePlayer.getCommandSenderName();
		MozeCore.logger.logInfo("Removing "+userName+" from scheduled checklists: Player disconnected.");
		PlayerChecksEvent.removePlayerFromLists(userName);
	}
}
