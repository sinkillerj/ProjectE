package moze_intel.projecte.events;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientCheckUpdatePKT;
import moze_intel.projecte.network.packets.ClientSyncPKT;
import moze_intel.projecte.playerData.AlchemicalBags;
import moze_intel.projecte.utils.AchievementHandler;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.Achievement;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class PlayerEvents 
{
	@SubscribeEvent
	public void playerConnect(PlayerLoggedInEvent event)
	{
		PacketHandler.sendTo(new ClientSyncPKT(), (EntityPlayerMP) event.player);
		PacketHandler.sendTo(new ClientCheckUpdatePKT(), (EntityPlayerMP) event.player);
	}
		
	
	@SubscribeEvent
	public void playerDisconnect(ClientDisconnectionFromServerEvent event)
	{
		String userName = Minecraft.getMinecraft().thePlayer.getCommandSenderName();
		PELogger.logInfo("Removing "+userName+" from scheduled checklists: Player disconnected.");
		PlayerChecksEvent.removePlayerFromLists(userName);
	}
}
