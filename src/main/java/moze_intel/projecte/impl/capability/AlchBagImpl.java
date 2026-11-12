package moze_intel.projecte.impl.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.impl.codec.PECodecHelper;
import moze_intel.projecte.network.PEStreamCodecs;
import moze_intel.projecte.network.packets.to_client.alch_bag.SyncAllBagDataPKT;
import moze_intel.projecte.network.packets.to_client.alch_bag.SyncBagsDataPKT;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AlchBagImpl implements IAlchBagProvider {

	private final Player player;

	public AlchBagImpl(Player player) {
		this.player = player;
	}

	private AlchemicalBagAttachment attachment() {
		return this.player.getData(PEAttachmentTypes.ALCHEMICAL_BAGS);
	}

	@NotNull
	@Override
	public PEItemStacksHandler getBag(@NotNull DyeColor color) {
		return attachment().getBag(color);
	}

	@Override
	public void sync(@NotNull ServerPlayer player, @NotNull Set<DyeColor> colors) {
		if (!colors.isEmpty()) {
			AlchemicalBagAttachment attachment = attachment();
			Map<DyeColor, PEItemStacksHandler> handlers = new EnumMap<>(DyeColor.class);
			for (DyeColor color : colors) {
				handlers.put(color, attachment.getBag(color));
			}
			PacketDistributor.sendToPlayer(player, new SyncBagsDataPKT(handlers));
		}
	}

	@Override
	public void syncAllBags(@NotNull ServerPlayer player) {
		PacketDistributor.sendToPlayer(player, new SyncAllBagDataPKT(attachment()));
	}

	public static class AlchemicalBagAttachment {

		private static final int BAG_SIZE = 104;
		public static final MapCodec<AlchemicalBagAttachment> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.unboundedMap(DyeColor.CODEC, PECodecHelper.MUTABLE_HANDLER_CODEC).fieldOf("inventories")
						.forGetter(attachment -> attachment.inventories)
		).apply(instance, map -> new AlchemicalBagAttachment(map.isEmpty() ? new EnumMap<>(DyeColor.class) : new EnumMap<>(map))));
		public static final Codec<AlchemicalBagAttachment> CODEC = MAP_CODEC.codec();
		public static final StreamCodec<RegistryFriendlyByteBuf, Map<DyeColor, PEItemStacksHandler>> MAP_STREAM_CODEC = ByteBufCodecs.map(
				ignored -> new EnumMap<>(DyeColor.class),
				DyeColor.STREAM_CODEC,
				PEStreamCodecs.handlerStreamCodec(BAG_SIZE)
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, AlchemicalBagAttachment> STREAM_CODEC = MAP_STREAM_CODEC.map(
				AlchemicalBagAttachment::new, attachment -> attachment.inventories
		);

		private final Map<DyeColor, PEItemStacksHandler> inventories;

		public AlchemicalBagAttachment(@Nullable IAttachmentHolder unused) {
			this(new EnumMap<>(DyeColor.class));
		}

		private AlchemicalBagAttachment(Map<DyeColor, PEItemStacksHandler> inventories) {
			this.inventories = inventories;
		}

		@Nullable
		public AlchemicalBagAttachment copy(IAttachmentHolder holder, HolderLookup.Provider registries) {
			AlchemicalBagAttachment copy = new AlchemicalBagAttachment(holder);
			for (Map.Entry<DyeColor, PEItemStacksHandler> entry : inventories.entrySet()) {
				copy.inventories.put(entry.getKey(), PEAttachmentTypes.copyHandler(entry.getValue(), PEItemStacksHandler::new));
			}
			return copy;
		}

		@NotNull
		public PEItemStacksHandler getBag(@NotNull DyeColor color) {
			return inventories.computeIfAbsent(color, c -> new PEItemStacksHandler(BAG_SIZE));
		}

		public void updateBags(Map<DyeColor, PEItemStacksHandler> handlers) {
			for (Map.Entry<DyeColor, PEItemStacksHandler> entry : handlers.entrySet()) {
				DyeColor color = entry.getKey();
				PEItemStacksHandler handler = entry.getValue();
				if (handler.getSlots() == BAG_SIZE) {
					inventories.put(color, handler);
				} else {
					PECore.LOGGER.warn("Received packet for updating {}, but the handler was of the wrong size. Expected: {}, Received: {}", color, BAG_SIZE, handler.getSlots());
				}
			}
		}
	}
}
