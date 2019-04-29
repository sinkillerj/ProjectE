package moze_intel.projecte.network.packets;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.playerData.Transmutation;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class SyncEmcPKT {
	private final EmcPKTInfo[] data;

	public SyncEmcPKT(EmcPKTInfo[] data)
	{
		this.data = data;
	}

	public static void encode(SyncEmcPKT pkt, PacketBuffer buf)
	{
		buf.writeVarInt(pkt.data.length);

		for (EmcPKTInfo info : pkt.data)
		{
			buf.writeInt(info.getId().toString().length());
			buf.writeString(info.getId().toString());
			buf.writeLong(info.getEmc());
		}
	}

	public static SyncEmcPKT decode(PacketBuffer buf)
	{
		int size = buf.readVarInt();
		EmcPKTInfo[] data = new EmcPKTInfo[size];

		for (int i = 0; i < size; i++)
		{
			int ssize = buf.readInt();
			data[i] = new EmcPKTInfo(NormalizedSimpleStack.deserializeFromString(buf.readString(ssize)), buf.readLong());
		}

		return new SyncEmcPKT(data);
	}

	public static class Handler
	{
		public static void handle(final SyncEmcPKT pkt, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> {
				PECore.LOGGER.info("Receiving EMC data from server.");
				EMCMapper.emc.clear();

				for (EmcPKTInfo info : pkt.data)
				{
					EMCMapper.emc.put(info.getId(), info.getEmc());
				}

				Transmutation.cacheFullKnowledge();
				FuelMapper.loadMap();
				PECore.refreshJEI();
			});
			ctx.get().setPacketHandled(true);
		}
	}

	public static class EmcPKTInfo {
		private NormalizedSimpleStack id;
		private long emc;

		public EmcPKTInfo(NormalizedSimpleStack normalizedSimpleStack, long emc) {
			this.id = normalizedSimpleStack;
			this.emc = emc;
		}

		public NormalizedSimpleStack getId() {
			return id;
		}

		public long getEmc() {
			return emc;
		}
	}
}
