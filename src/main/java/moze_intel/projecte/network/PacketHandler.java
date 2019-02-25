package moze_intel.projecte.network;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.network.packets.*;
import moze_intel.projecte.network.packets.SyncEmcPKT.EmcPKTInfo;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;

public final class PacketHandler
{
	private static final SimpleNetworkWrapper HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel(PECore.MODID);

	public static void register()
	{
		int disc = 0;
		HANDLER.registerMessage(SyncEmcPKT.Handler.class, SyncEmcPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(KeyPressPKT.Handler.class, KeyPressPKT.class, disc++, Side.SERVER);
		HANDLER.registerMessage(StepHeightPKT.Handler.class, StepHeightPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(SetFlyPKT.Handler.class, SetFlyPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(KnowledgeSyncPKT.Handler.class, KnowledgeSyncPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(CheckUpdatePKT.Handler.class, CheckUpdatePKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(SyncBagDataPKT.Handler.class, SyncBagDataPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(SearchUpdatePKT.Handler.class, SearchUpdatePKT.class, disc++, Side.SERVER);
		HANDLER.registerMessage(KnowledgeClearPKT.Handler.class, KnowledgeClearPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(UpdateGemModePKT.Handler.class, UpdateGemModePKT.class, disc++, Side.SERVER);
		HANDLER.registerMessage(UpdateWindowIntPKT.Handler.class, UpdateWindowIntPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(UpdateWindowLongPKT.Handler.class, UpdateWindowLongPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(CooldownResetPKT.Handler.class, CooldownResetPKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(LeftClickArchangelPKT.Handler.class, LeftClickArchangelPKT.class, disc++, Side.SERVER);
		HANDLER.registerMessage(SyncCovalencePKT.Handler.class, SyncCovalencePKT.class, disc++, Side.CLIENT);
		HANDLER.registerMessage(ShowBagPKT.Handler.class, ShowBagPKT.class, disc++, Side.CLIENT);
	}

	public static void sendProgressBarUpdateInt(IContainerListener listener, Container container, int propId, int propVal)
	{
		if (listener instanceof EntityPlayerMP)
		{
			sendTo(new UpdateWindowIntPKT((short) container.windowId, (short) propId, propVal), (EntityPlayerMP) listener);
		}
	}

	public static void sendProgressBarUpdateLong(IContainerListener listener, Container container, int propId, long propVal)
	{
		if (listener instanceof EntityPlayerMP)
		{
			sendTo(new UpdateWindowLongPKT((short) container.windowId, (short) propId, propVal), (EntityPlayerMP) listener);
		}
	}

	public static void sendNonLocal(IMessage msg, EntityPlayerMP player)
	{
		if (player.server.isDedicatedServer() || !player.getName().equals(player.server.getServerOwner()))
		{
			HANDLER.sendTo(msg, player);
		}
	}

	public static void sendFragmentedEmcPacket(EntityPlayerMP player)
	{
		sendNonLocal(new SyncEmcPKT(serializeEmcData()), player);
	}

	public static void sendFragmentedEmcPacketToAll()
	{
		SyncEmcPKT pkt = new SyncEmcPKT(serializeEmcData());
		for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
		{
			sendNonLocal(pkt, player);
		}
	}

	private static EmcPKTInfo[] serializeEmcData()
	{
		EmcPKTInfo[] ret = new EmcPKTInfo[EMCMapper.emc.size()];
		int i = 0;
		for (Map.Entry<SimpleStack, Long> entry : EMCMapper.emc.entrySet())
		{
			SimpleStack stack = entry.getKey();
			int id = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(stack.id));
			ret[i] = new EmcPKTInfo(id, stack.damage, entry.getValue());
			i++;
		}
		PECore.debugLog("EMC data size: {} bytes", ret.length * (2 * 4 + 8));
		return ret;
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
		if (!(player instanceof FakePlayer))
		{
			HANDLER.sendTo(msg, player);
		}
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
