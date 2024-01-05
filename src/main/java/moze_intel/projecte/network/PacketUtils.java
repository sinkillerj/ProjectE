package moze_intel.projecte.network;

import io.netty.buffer.Unpooled;
import java.util.Optional;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.network.packets.to_client.SyncEmcPKT;
import moze_intel.projecte.network.packets.to_client.SyncEmcPKT.EmcPKTInfo;
import moze_intel.projecte.network.packets.to_client.SyncFuelMapperPKT;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * Heavily based off of Mekanism's packet utils
 */
public class PacketUtils {

	private PacketUtils() {
	}

	//TODO - 1.20.4: SP: Re-evaluate use cases of this and if there is a better way to handle them
	public static <OBJ> OBJ read(byte[] rawData, FriendlyByteBuf.Reader<OBJ> deserializer) {
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.wrappedBuffer(rawData));
		try {
			return deserializer.apply(buffer);
		} finally {
			buffer.release();
		}
	}

	public static <CLASS> Optional<CLASS> blockEntity(IPayloadContext context, BlockPos pos, Class<CLASS> clazz) {
		return blockEntity(context, pos)
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}

	public static Optional<BlockEntity> blockEntity(IPayloadContext context, BlockPos pos) {
		return context.level().map(level -> WorldHelper.getBlockEntity(level, pos));
	}

	public static <CLASS extends AbstractContainerMenu> Optional<CLASS> container(IPayloadContext context, Class<CLASS> clazz) {
		return context.player()
				.map(player -> player.containerMenu)
				.filter(clazz::isInstance)
				.map(clazz::cast);
	}

	private static boolean isLocal(ServerPlayer player) {
		return player.server.isSingleplayerOwner(player.getGameProfile());
	}

	private static void sendFragmentedEmcPacket(ServerPlayer player, SyncEmcPKT pkt, SyncFuelMapperPKT fuelPkt) {
		if (!isLocal(player)) {
			sendTo(pkt, player);
			sendTo(fuelPkt, player);
		}
	}

	public static void sendFragmentedEmcPacket(ServerPlayer player) {
		sendFragmentedEmcPacket(player, new SyncEmcPKT(serializeEmcData()), FuelMapper.getSyncPacket());
	}

	public static void sendFragmentedEmcPacketToAll() {
		if (ServerLifecycleHooks.getCurrentServer() != null) {
			SyncEmcPKT pkt = new SyncEmcPKT(serializeEmcData());
			SyncFuelMapperPKT fuelPkt = FuelMapper.getSyncPacket();
			for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
				sendFragmentedEmcPacket(player, pkt, fuelPkt);
			}
		}
	}

	private static EmcPKTInfo[] serializeEmcData() {
		EmcPKTInfo[] data = EMCMappingHandler.createPacketData();
		//Simulate encoding the EMC packet to get an accurate size
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		try {
			int index = buf.writerIndex();
			new SyncEmcPKT(data).write(buf);
			PECore.debugLog("EMC data size: {} bytes", buf.writerIndex() - index);
		} finally {
			buf.release();
		}
		return data;
	}

	/**
	 * Send this message to the specified player.
	 *
	 * @param message - the message to send
	 * @param player  - the player to send it to
	 */
	public static <MSG extends CustomPacketPayload> void sendTo(MSG message, ServerPlayer player) {
		PacketDistributor.PLAYER.with(player).send(message);
	}

	/**
	 * Send this message to everyone connected to the server.
	 *
	 * @param message - message to send
	 */
	public static <MSG extends CustomPacketPayload> void sendToAll(MSG message) {
		PacketDistributor.ALL.noArg().send(message);
	}

	/**
	 * Send this message to everyone connected to the server if the server has loaded.
	 *
	 * @param message - message to send
	 *
	 * @apiNote This is useful for reload listeners
	 */
	public static <MSG extends CustomPacketPayload> void sendToAllIfLoaded(MSG message) {
		if (ServerLifecycleHooks.getCurrentServer() != null) {
			//If the server has loaded, send to all players
			sendToAll(message);
		}
	}

	/**
	 * Send this message to everyone within the supplied dimension.
	 *
	 * @param message   - the message to send
	 * @param dimension - the dimension to target
	 */
	public static <MSG extends CustomPacketPayload> void sendToDimension(MSG message, ResourceKey<Level> dimension) {
		PacketDistributor.DIMENSION.with(dimension).send(message);
	}

	/**
	 * Send this message to the server.
	 *
	 * @param message - the message to send
	 */
	public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
		PacketDistributor.SERVER.noArg().send(message);
	}

	public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, Entity entity) {
		PacketDistributor.TRACKING_ENTITY.with(entity).send(message);
	}

	public static <MSG extends CustomPacketPayload> void sendToAllTrackingAndSelf(MSG message, Entity entity) {
		PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity).send(message);
	}

	public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, BlockEntity tile) {
		sendToAllTracking(message, tile.getLevel(), tile.getBlockPos());
	}

	public static <MSG extends CustomPacketPayload> void sendToAllTracking(MSG message, Level world, BlockPos pos) {
		if (world instanceof ServerLevel level) {
			//If we have a ServerWorld just directly figure out the ChunkPos to not require looking up the chunk
			// This provides a decent performance boost over using the packet distributor
			level.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(p -> sendTo(message, p));
		} else {
			//Otherwise, fallback to entities tracking the chunk if some mod did something odd and our world is not a ServerWorld
			PacketDistributor.TRACKING_CHUNK.with(world.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))).send(message);
		}
	}
}