package sinkillerj.projecte.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import sinkillerj.projecte.handlers.PlayerChecks;
import sinkillerj.projecte.network.PacketHandler;
import sinkillerj.projecte.network.packets.ClientCheckUpdatePKT;
import sinkillerj.projecte.utils.PELogger;
import sinkillerj.projecte.handlers.PlayerTimers;
import net.minecraft.entity.player.EntityPlayerMP;

public class ConnectionHandler
{
	@SubscribeEvent
	public void playerConnect(PlayerLoggedInEvent event)
	{
		//PacketHandler.sendTo(new ClientSyncPKT(), (EntityPlayerMP) event.player);
        PacketHandler.sendFragmentedEmcPacket((EntityPlayerMP) event.player);
		PacketHandler.sendTo(new ClientCheckUpdatePKT(), (EntityPlayerMP) event.player);

        PlayerTimers.registerPlayer(event.player);

	}

    @SubscribeEvent
    public void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event)
    {
        PlayerTimers.removePlayer(event.player);

        PELogger.logInfo("Removing " + event.player.getCommandSenderName() + " from scheduled checklists: Player disconnected.");
        PlayerChecks.removePlayerFromLists(event.player.getCommandSenderName());
    }
		
	
	/*@SubscribeEvent
	public void playerDisconnect(ClientDisconnectionFromServerEvent event)
	{
		String userName = Minecraft.getMinecraft().thePlayer.getCommandSenderName();
		PELogger.logInfo("Removing " + userName + " from scheduled checklists: Player disconnected.");
		PlayerChecksEvent.removePlayerFromLists(userName);
	}*/
}
