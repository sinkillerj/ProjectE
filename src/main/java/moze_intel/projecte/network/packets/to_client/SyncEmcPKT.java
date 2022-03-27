package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.Nullable;

public class SyncEmcPKT implements IPEPacket {

	private final EmcPKTInfo[] data;

	public SyncEmcPKT(EmcPKTInfo[] data) {
		this.data = data;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		PECore.LOGGER.info("Receiving EMC data from server.");
		EMCMappingHandler.fromPacket(data);
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(data.length);
		for (EmcPKTInfo info : data) {
			buffer.writeRegistryId(info.item);
			buffer.writeNbt(info.nbt());
			buffer.writeVarLong(info.emc());
		}
	}

	public static SyncEmcPKT decode(FriendlyByteBuf buffer) {
		int size = buffer.readVarInt();
		EmcPKTInfo[] data = new EmcPKTInfo[size];
		for (int i = 0; i < size; i++) {
			data[i] = new EmcPKTInfo(buffer.readRegistryId(), buffer.readNbt(), buffer.readVarLong());
		}
		return new SyncEmcPKT(data);
	}

	public record EmcPKTInfo(Item item, @Nullable CompoundTag nbt, long emc) {}
}