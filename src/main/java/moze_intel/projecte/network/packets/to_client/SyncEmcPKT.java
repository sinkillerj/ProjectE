package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncEmcPKT(EmcPKTInfo[] data) implements IPEPacket<IPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("sync_emc");

	public SyncEmcPKT(FriendlyByteBuf buffer) {
		this(buffer.readArray(EmcPKTInfo[]::new, buf -> new EmcPKTInfo(ItemInfo.read(buf), buf.readVarLong())));
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(IPayloadContext context) {
		PECore.LOGGER.info("Receiving EMC data from server.");
		EMCMappingHandler.fromPacket(data);
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeArray(data, (buf, info) -> {
			info.item.write(buf);
			buf.writeVarLong(info.emc());
		});
	}

	public record EmcPKTInfo(ItemInfo item, long emc) {
	}
}