package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record SyncEmcPKT(EmcPKTInfo[] data) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("sync_emc");

	public SyncEmcPKT(FriendlyByteBuf buffer) {
		this(buffer.readArray(EmcPKTInfo[]::new, buf -> new EmcPKTInfo(buf.readById(BuiltInRegistries.ITEM), buf.readNbt(), buf.readVarLong())));
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(PlayPayloadContext context) {
		PECore.LOGGER.info("Receiving EMC data from server.");
		EMCMappingHandler.fromPacket(data);
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeArray(data, (buf, info) -> {
			buf.writeId(BuiltInRegistries.ITEM, info.item);
			buf.writeNbt(info.nbt());
			buf.writeVarLong(info.emc());
		});
	}

	public record EmcPKTInfo(Item item, @Nullable CompoundTag nbt, long emc) {}
}