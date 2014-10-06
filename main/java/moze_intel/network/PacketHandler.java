package moze_intel.network;

import net.minecraft.entity.player.EntityPlayerMP;
import moze_intel.network.packets.*;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public abstract class PacketHandler 
{
	private static final SimpleNetworkWrapper HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel("projecte");
	
	public static void register()
	{
		HANDLER.registerMessage(ClientSyncPKT.class, ClientSyncPKT.class, 0, Side.CLIENT);
    	HANDLER.registerMessage(KeyPressPKT.class, KeyPressPKT.class, 1, Side.SERVER);
    	HANDLER.registerMessage(ParticlePKT.class, ParticlePKT.class, 2, Side.CLIENT);
    	HANDLER.registerMessage(SwingItemPKT.class, SwingItemPKT.class, 3, Side.CLIENT);
    	HANDLER.registerMessage(StepHeightPKT.class, StepHeightPKT.class, 4, Side.CLIENT);
    	HANDLER.registerMessage(SetFlyPKT.class, SetFlyPKT.class, 5, Side.CLIENT);
    	HANDLER.registerMessage(ClientKnowledgeSyncPKT.class, ClientKnowledgeSyncPKT.class, 6, Side.CLIENT);
    	HANDLER.registerMessage(TTableSyncPKT.class, TTableSyncPKT.class, 7, Side.CLIENT);
    	HANDLER.registerMessage(CondenserSyncPKT.class, CondenserSyncPKT.class, 8, Side.CLIENT);
    	HANDLER.registerMessage(CollectorSyncPKT.class, CollectorSyncPKT.class, 9, Side.CLIENT);
    	HANDLER.registerMessage(RelaySyncPKT.class, RelaySyncPKT.class, 10, Side.CLIENT);
    	HANDLER.registerMessage(ClientCheckUpdatePKT.class, ClientCheckUpdatePKT.class, 11, Side.CLIENT);
    	HANDLER.registerMessage(ClientSyncBagDataPKT.class, ClientSyncBagDataPKT.class, 12, Side.CLIENT);
    	HANDLER.registerMessage(SearchUpdatePKT.class, SearchUpdatePKT.class, 13, Side.SERVER);
    	HANDLER.registerMessage(ClientKnowledgeClearPKT.class, ClientKnowledgeClearPKT.class, 14, Side.CLIENT);
	}
	
	/**
	 * Sends a packet to the server.<br>
	 * Must be called Client side. 
	 */
	public static void sendToServer(IMessage msg)
	{
		HANDLER.sendToServer(msg);
	}
	
	/**
	 * Sends a packet to all the clients.<br>
	 * Must be called Server side.
	 */
	public static void sendToAll(IMessage msg)
	{
		HANDLER.sendToAll(msg);
	}
	
	/**
	 * Send a packet to all players around a specific point.<br>
	 * Must be called Server side. 
	 */
	public static void sendToAllAround(IMessage msg, TargetPoint point)
	{
		HANDLER.sendToAllAround(msg, point);
	}
	
	/**
	 * Send a packet to a specific player.<br>
	 * Must be called Server side. 
	 */
	public static void sendTo(IMessage msg, EntityPlayerMP player)
	{
		HANDLER.sendTo(msg, player);
	}
	
	/**
	 * Send a packet to all the players in the specified dimension.<br>
	 *  Must be called Server side.
	 */
	public static void sendToDimension(IMessage msg, int dimension)
	{
		HANDLER.sendToDimension(msg, dimension);
	}
}
