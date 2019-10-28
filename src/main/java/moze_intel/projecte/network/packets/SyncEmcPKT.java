package moze_intel.projecte.network.packets;

import java.util.function.Supplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.playerData.Transmutation;
import net.minecraft.item.Item;
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
			buf.writeVarInt(info.getId());
			buf.writeLong(info.getEmc());
		}
	}

	public static SyncEmcPKT decode(PacketBuffer buf) {
		int size = buf.readVarInt();
		EmcPKTInfo[] data = new EmcPKTInfo[size];
		for (int i = 0; i < size; i++) {
			data[i] = new EmcPKTInfo(buf.readVarInt(), buf.readLong());
		}
		return new SyncEmcPKT(data);
	}

	public static class Handler {

		public static void handle(final SyncEmcPKT pkt, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				PECore.LOGGER.info("Receiving EMC data from server.");
				EMCMappingHandler.emc.clear();

				for (EmcPKTInfo info : pkt.data) {
					Item i = Item.getItemById(info.getId());
					EMCMappingHandler.emc.put(i, info.getEmc());
				}

				Transmutation.cacheFullKnowledge();
				FuelMapper.loadMap();
			});
			ctx.get().setPacketHandled(true);
		}
	}

	public static class EmcPKTInfo {

		private int id;
		private long emc;

		public EmcPKTInfo(int id, long emc) {
			this.id = id;
			this.emc = emc;
		}

		public int getId() {
			return id;
		}

		public long getEmc() {
			return emc;
		}
	}
}