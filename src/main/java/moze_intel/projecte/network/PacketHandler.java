package moze_intel.projecte.network;

import io.netty.buffer.Unpooled;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.network.packets.CooldownResetPKT;
import moze_intel.projecte.network.packets.KeyPressPKT;
import moze_intel.projecte.network.packets.KnowledgeClearPKT;
import moze_intel.projecte.network.packets.KnowledgeSyncPKT;
import moze_intel.projecte.network.packets.LeftClickArchangelPKT;
import moze_intel.projecte.network.packets.SearchUpdatePKT;
import moze_intel.projecte.network.packets.SetFlyPKT;
import moze_intel.projecte.network.packets.StepHeightPKT;
import moze_intel.projecte.network.packets.SyncBagDataPKT;
import moze_intel.projecte.network.packets.SyncEmcPKT;
import moze_intel.projecte.network.packets.SyncEmcPKT.EmcPKTInfo;
import moze_intel.projecte.network.packets.UpdateCondenserLockPKT;
import moze_intel.projecte.network.packets.UpdateGemModePKT;
import moze_intel.projecte.network.packets.UpdateWindowIntPKT;
import moze_intel.projecte.network.packets.UpdateWindowLongPKT;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public final class PacketHandler {

	private static final String PROTOCOL_VERSION = Integer.toString(1);
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
			.named(PECore.rl("main_channel"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	private static int index;

	public static void register() {
		registerMessage(CooldownResetPKT.class, CooldownResetPKT::encode, CooldownResetPKT::decode, CooldownResetPKT.Handler::handle);
		registerMessage(KeyPressPKT.class, KeyPressPKT::encode, KeyPressPKT::decode, KeyPressPKT.Handler::handle);
		registerMessage(KnowledgeClearPKT.class, KnowledgeClearPKT::encode, KnowledgeClearPKT::decode, KnowledgeClearPKT.Handler::handle);
		registerMessage(KnowledgeSyncPKT.class, KnowledgeSyncPKT::encode, KnowledgeSyncPKT::decode, KnowledgeSyncPKT.Handler::handle);
		registerMessage(LeftClickArchangelPKT.class, LeftClickArchangelPKT::encode, LeftClickArchangelPKT::decode, LeftClickArchangelPKT.Handler::handle);
		registerMessage(SearchUpdatePKT.class, SearchUpdatePKT::encode, SearchUpdatePKT::decode, SearchUpdatePKT.Handler::handle);
		registerMessage(SetFlyPKT.class, SetFlyPKT::encode, SetFlyPKT::decode, SetFlyPKT.Handler::handle);
		registerMessage(StepHeightPKT.class, StepHeightPKT::encode, StepHeightPKT::decode, StepHeightPKT.Handler::handle);
		registerMessage(SyncBagDataPKT.class, SyncBagDataPKT::encode, SyncBagDataPKT::decode, SyncBagDataPKT.Handler::handle);
		registerMessage(SyncEmcPKT.class, SyncEmcPKT::encode, SyncEmcPKT::decode, SyncEmcPKT.Handler::handle);
		registerMessage(UpdateCondenserLockPKT.class, UpdateCondenserLockPKT::encode, UpdateCondenserLockPKT::decode, UpdateCondenserLockPKT.Handler::handle);
		registerMessage(UpdateGemModePKT.class, UpdateGemModePKT::encode, UpdateGemModePKT::decode, UpdateGemModePKT.Handler::handle);
		registerMessage(UpdateWindowIntPKT.class, UpdateWindowIntPKT::encode, UpdateWindowIntPKT::decode, UpdateWindowIntPKT.Handler::handle);
		registerMessage(UpdateWindowLongPKT.class, UpdateWindowLongPKT::encode, UpdateWindowLongPKT::decode, UpdateWindowLongPKT.Handler::handle);
	}

	private static <MSG> void registerMessage(Class<MSG> type, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder,
			BiConsumer<MSG, Supplier<Context>> consumer) {
		HANDLER.registerMessage(index++, type, encoder, decoder, consumer);
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

	public static void sendLockSlotUpdate(IContainerListener listener, Container container, ItemInfo lockInfo) {
		if (listener instanceof ServerPlayerEntity) {
			sendTo(new UpdateCondenserLockPKT((short) container.windowId, lockInfo), (ServerPlayerEntity) listener);
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
		if (ServerLifecycleHooks.getCurrentServer() != null) {
			SyncEmcPKT pkt = new SyncEmcPKT(serializeEmcData());
			for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
				sendNonLocal(pkt, player);
			}
		}
	}

	private static EmcPKTInfo[] serializeEmcData() {
		EmcPKTInfo[] data = EMCMappingHandler.createPacketData();
		//Simulate encoding the EMC packet to get an accurate size
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		int index = buf.writerIndex();
		SyncEmcPKT.encode(new SyncEmcPKT(data), buf);
		PECore.debugLog("EMC data size: {} bytes", buf.writerIndex() - index);
		buf.release();
		return data;
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