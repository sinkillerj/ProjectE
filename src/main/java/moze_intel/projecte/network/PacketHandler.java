package moze_intel.projecte.network;

import io.netty.buffer.Unpooled;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.ItemInfo;
import moze_intel.projecte.network.packets.CooldownResetPKT;
import moze_intel.projecte.network.packets.KeyPressPKT;
import moze_intel.projecte.network.packets.KnowledgeClearPKT;
import moze_intel.projecte.network.packets.KnowledgeSyncPKT;
import moze_intel.projecte.network.packets.LeftClickArchangelPKT;
import moze_intel.projecte.network.packets.SearchUpdatePKT;
import moze_intel.projecte.network.packets.SetFlyPKT;
import moze_intel.projecte.network.packets.StepHeightPKT;
import moze_intel.projecte.network.packets.SyncBagDataPKT;
import moze_intel.projecte.network.packets.SyncCovalencePKT;
import moze_intel.projecte.network.packets.SyncEmcPKT;
import moze_intel.projecte.network.packets.SyncEmcPKT.EmcPKTInfo;
import moze_intel.projecte.network.packets.UpdateGemModePKT;
import moze_intel.projecte.network.packets.UpdateWindowIntPKT;
import moze_intel.projecte.network.packets.UpdateWindowLongPKT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public final class PacketHandler {

	private static final String PROTOCOL_VERSION = Integer.toString(1);
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(PECore.MODID, "main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public static void register() {
		int disc = 0;

		HANDLER.registerMessage(disc++, SyncEmcPKT.class, SyncEmcPKT::encode, SyncEmcPKT::decode, SyncEmcPKT.Handler::handle);
		HANDLER.registerMessage(disc++, KeyPressPKT.class, KeyPressPKT::encode, KeyPressPKT::decode, KeyPressPKT.Handler::handle);
		HANDLER.registerMessage(disc++, StepHeightPKT.class, StepHeightPKT::encode, StepHeightPKT::decode, StepHeightPKT.Handler::handle);
		HANDLER.registerMessage(disc++, SetFlyPKT.class, SetFlyPKT::encode, SetFlyPKT::decode, SetFlyPKT.Handler::handle);
		HANDLER.registerMessage(disc++, KnowledgeSyncPKT.class, KnowledgeSyncPKT::encode, KnowledgeSyncPKT::decode, KnowledgeSyncPKT.Handler::handle);
		HANDLER.registerMessage(disc++, SyncBagDataPKT.class, SyncBagDataPKT::encode, SyncBagDataPKT::decode, SyncBagDataPKT.Handler::handle);
		HANDLER.registerMessage(disc++, SearchUpdatePKT.class, SearchUpdatePKT::encode, SearchUpdatePKT::decode, SearchUpdatePKT.Handler::handle);
		HANDLER.registerMessage(disc++, KnowledgeClearPKT.class, KnowledgeClearPKT::encode, KnowledgeClearPKT::decode, KnowledgeClearPKT.Handler::handle);
		HANDLER.registerMessage(disc++, UpdateGemModePKT.class, UpdateGemModePKT::encode, UpdateGemModePKT::decode, UpdateGemModePKT.Handler::handle);
		HANDLER.registerMessage(disc++, UpdateWindowIntPKT.class, UpdateWindowIntPKT::encode, UpdateWindowIntPKT::decode, UpdateWindowIntPKT.Handler::handle);
		HANDLER.registerMessage(disc++, UpdateWindowLongPKT.class, UpdateWindowLongPKT::encode, UpdateWindowLongPKT::decode, UpdateWindowLongPKT.Handler::handle);
		HANDLER.registerMessage(disc++, CooldownResetPKT.class, CooldownResetPKT::encode, CooldownResetPKT::decode, CooldownResetPKT.Handler::handle);
		HANDLER.registerMessage(disc++, LeftClickArchangelPKT.class, LeftClickArchangelPKT::encode, LeftClickArchangelPKT::decode, LeftClickArchangelPKT.Handler::handle);
		HANDLER.registerMessage(disc++, SyncCovalencePKT.class, SyncCovalencePKT::encode, SyncCovalencePKT::decode, SyncCovalencePKT.Handler::handle);
	}

	public static void sendProgressBarUpdateInt(IContainerListener listener, Container container, int propId, int propVal) {
		if (listener instanceof ServerPlayerEntity) {
			sendTo(new UpdateWindowIntPKT((short) container.windowId, (short) propId, propVal), (ServerPlayerEntity) listener);
		}
	}

	public static void sendProgressBarUpdateLong(IContainerListener listener, Container container, int propId, long propVal) {
		if (listener instanceof ServerPlayerEntity) {
			sendTo(new UpdateWindowLongPKT((short) container.windowId, (short) propId, propVal), (ServerPlayerEntity) listener);
		}
	}

	public static void sendNonLocal(Object msg, ServerPlayerEntity player) {
		if (player.server.isDedicatedServer() || !player.getGameProfile().getName().equals(player.server.getServerOwner())) {
			HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	public static void sendFragmentedEmcPacket(ServerPlayerEntity player) {
		sendNonLocal(new SyncEmcPKT(serializeEmcData()), player);
	}

	public static void sendFragmentedEmcPacketToAll() {
		SyncEmcPKT pkt = new SyncEmcPKT(serializeEmcData());
		for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
			sendNonLocal(pkt, player);
		}
	}

	private static EmcPKTInfo[] serializeEmcData() {
		EmcPKTInfo[] ret = new EmcPKTInfo[EMCMappingHandler.emc.size()];
		int i = 0;
		for (Map.Entry<ItemInfo, Long> entry : EMCMappingHandler.emc.entrySet()) {
			ItemInfo info = entry.getKey();
			ret[i] = new EmcPKTInfo(info.getItem(), info.getNBT(), entry.getValue());
			i++;
		}
		//Simulate encoding the EMC packet to get an accurate size
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		int index = buf.writerIndex();
		SyncEmcPKT.encode(new SyncEmcPKT(ret), buf);
		PECore.debugLog("EMC data size: {} bytes", (buf.writerIndex() - index));
		buf.release();
		return ret;
	}

	/**
	 * Sends a packet to the server.<br> Must be called Client side.
	 */
	public static void sendToServer(Object msg) {
		HANDLER.sendToServer(msg);
	}

	/**
	 * Send a packet to a specific player.<br> Must be called Server side.
	 */
	public static void sendTo(Object msg, ServerPlayerEntity player) {
		if (!(player instanceof FakePlayer)) {
			HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
		}
	}
}