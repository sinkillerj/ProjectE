package moze_intel.projecte.network.packets.to_client;

import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.TagCollectionManager;
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
		//TODO - 1.16: Figure out if this is correct or if it should somehow reference ItemTags instead
		// Alternatively we potentially should listen to TagsUpdatedEvent.Vanilla for when to update it?
		// Or maybe both where we do it and in the TagsUpdatedEvent.Vanilla, in theory the tags updated
		// event will actually fire *after* emc calculations? Because it is after the named tags get populated
		// which is after all the reload listeners happen. We will need to validate if this is also the proper
		// time on the client. This may not actually be correct for the initial login to a server as the tags
		// will be synced before the client is sent EMC values for things
		FuelMapper.loadMap(TagCollectionManager.getManager());
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