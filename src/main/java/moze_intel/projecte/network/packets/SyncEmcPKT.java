package moze_intel.projecte.network.packets;

import java.util.function.Supplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.FuelMapper;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncEmcPKT {

	private final EmcPKTInfo[] data;

	public SyncEmcPKT(EmcPKTInfo[] data) {
		this.data = data;
	}

	public static void encode(SyncEmcPKT pkt, PacketBuffer buf) {
		buf.writeVarInt(pkt.data.length);
		for (EmcPKTInfo info : pkt.data) {
			buf.writeRegistryId(info.getItem());
			buf.writeCompoundTag(info.getNbt());
			buf.writeVarLong(info.getEmc());
		}
	}

	public static SyncEmcPKT decode(PacketBuffer buf) {
		int size = buf.readVarInt();
		EmcPKTInfo[] data = new EmcPKTInfo[size];
		for (int i = 0; i < size; i++) {
			data[i] = new EmcPKTInfo(buf.readRegistryId(), buf.readCompoundTag(), buf.readVarLong());
		}
		return new SyncEmcPKT(data);
	}

	public static class Handler {

		public static void handle(final SyncEmcPKT pkt, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				PECore.LOGGER.info("Receiving EMC data from server.");
				EMCMappingHandler.fromPacket(pkt.data);
				FuelMapper.loadMap();
			});
			ctx.get().setPacketHandled(true);
		}
	}

	public static class EmcPKTInfo {

		private Item item;
		private long emc;
		private CompoundNBT nbt;

		public EmcPKTInfo(Item item, CompoundNBT nbt, long emc) {
			this.item = item;
			this.emc = emc;
			this.nbt = nbt;
		}

		public Item getItem() {
			return item;
		}

		public long getEmc() {
			return emc;
		}

		public CompoundNBT getNbt() {
			return nbt;
		}
	}
}