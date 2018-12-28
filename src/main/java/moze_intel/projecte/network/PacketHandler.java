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
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.Map;

public final class PacketHandler
{
	private static final String PROTOCOL_VERSION = Integer.toString(1);
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(PECore.MODID, "main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public static void register()
	{
		int disc = 0;

		HANDLER.registerMessage(disc++, SyncEmcPKT.class, SyncEmcPKT::encode, SyncEmcPKT::decode, SyncEmcPKT.Handler::handle);
		HANDLER.registerMessage(disc++, KeyPressPKT.class, KeyPressPKT::encode, KeyPressPKT::decode, KeyPressPKT.Handler::handle);
		HANDLER.registerMessage(disc++, StepHeightPKT.class, StepHeightPKT::encode, StepHeightPKT::decode, StepHeightPKT.Handler::handle);
		HANDLER.registerMessage(disc++, SetFlyPKT.class, SetFlyPKT::encode, SetFlyPKT::decode, SetFlyPKT.Handler::handle);
		HANDLER.registerMessage(disc++, KnowledgeSyncPKT.class, KnowledgeSyncPKT::encode, KnowledgeSyncPKT::decode, KnowledgeSyncPKT.Handler::handle);
		HANDLER.registerMessage(disc++, CheckUpdatePKT.class, CheckUpdatePKT::encode, CheckUpdatePKT::decode, CheckUpdatePKT.Handler::handle);
		HANDLER.registerMessage(disc++, SyncBagDataPKT.class, SyncBagDataPKT::encode, SyncBagDataPKT::decode, SyncBagDataPKT.Handler::handle);
		HANDLER.registerMessage(disc++, SearchUpdatePKT.class, SearchUpdatePKT::encode, SearchUpdatePKT::decode, SearchUpdatePKT.Handler::handle);
		HANDLER.registerMessage(disc++, KnowledgeClearPKT.class, KnowledgeClearPKT::encode, KnowledgeClearPKT::decode, KnowledgeClearPKT.Handler::handle);
		HANDLER.registerMessage(disc++, UpdateGemModePKT.class, UpdateGemModePKT::encode, UpdateGemModePKT::decode, UpdateGemModePKT.Handler::handle);
		HANDLER.registerMessage(disc++, UpdateWindowIntPKT.class, UpdateWindowIntPKT::encode, UpdateWindowIntPKT::decode, UpdateWindowIntPKT.Handler::handle);
		HANDLER.registerMessage(disc++, UpdateWindowLongPKT.class, UpdateWindowLongPKT::encode, UpdateWindowLongPKT::decode, UpdateWindowLongPKT.Handler::handle);
		HANDLER.registerMessage(disc++, CooldownResetPKT.class, CooldownResetPKT::encode, CooldownResetPKT::decode, CooldownResetPKT.Handler::handle);
		HANDLER.registerMessage(disc++, LeftClickArchangelPKT.class, LeftClickArchangelPKT::encode, LeftClickArchangelPKT::decode, LeftClickArchangelPKT.Handler::handle);
		HANDLER.registerMessage(disc++, SyncCovalencePKT.class, SyncCovalencePKT::encode, SyncCovalencePKT::decode, SyncCovalencePKT.Handler::handle);
		HANDLER.registerMessage(disc++, ShowBagPKT.class, ShowBagPKT::encode, ShowBagPKT::decode, ShowBagPKT.Handler::handle);
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
			HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	public static void sendFragmentedEmcPacket(EntityPlayerMP player)
	{
		sendNonLocal(new SyncEmcPKT(serializeEmcData()), player);
	}

	public static void sendFragmentedEmcPacketToAll()
	{
		SyncEmcPKT pkt = new SyncEmcPKT(serializeEmcData());
		for (EntityPlayerMP player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
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
			int id = Item.REGISTRY.getId(Item.REGISTRY.get(stack.id));
			ret[i] = new EmcPKTInfo(id, entry.getValue());
			i++;
		}
		PECore.debugLog("EMC data size: {} bytes", ret.length * (4 + 8));
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
	 * Send a packet to a specific player.<br>
	 * Must be called Server side. 
	 */
	public static void sendTo(IMessage msg, EntityPlayerMP player)
	{
		if (!(player instanceof FakePlayer))
		{
			HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}
