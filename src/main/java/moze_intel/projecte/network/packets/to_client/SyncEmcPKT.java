package moze_intel.projecte.network.packets.to_client;

import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SyncEmcPKT implements IPEPacket {

	private final EmcPKTInfo[] data;

	public SyncEmcPKT(EmcPKTInfo[] data) {
		this.data = data;
	}

	@Override
	public void handle(Context context) {
		PECore.LOGGER.info("Receiving EMC data from server.");
		EMCMappingHandler.fromPacket(data);
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeVarInt(data.length);
		for (EmcPKTInfo info : data) {
			buffer.writeRegistryId(info.getItem());
			buffer.writeCompoundTag(info.getNbt());
			buffer.writeVarLong(info.getEmc());
		}
	}

	public static SyncEmcPKT decode(PacketBuffer buffer) {
		int size = buffer.readVarInt();
		EmcPKTInfo[] data = new EmcPKTInfo[size];
		for (int i = 0; i < size; i++) {
			data[i] = new EmcPKTInfo(buffer.readRegistryId(), buffer.readCompoundTag(), buffer.readVarLong());
		}
		return new SyncEmcPKT(data);
	}

	public static class EmcPKTInfo {

		private final Item item;
		private final long emc;
		@Nullable
		private final CompoundNBT nbt;

		public EmcPKTInfo(Item item, @Nullable CompoundNBT nbt, long emc) {
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

		@Nullable
		public CompoundNBT getNbt() {
			return nbt;
		}
	}
}