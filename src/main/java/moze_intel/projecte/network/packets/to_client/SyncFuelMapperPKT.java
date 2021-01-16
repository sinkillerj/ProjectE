package moze_intel.projecte.network.packets.to_client;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SyncFuelMapperPKT implements IPEPacket {

	private final List<Item> items;

	public SyncFuelMapperPKT(List<Item> items) {
		this.items = items;
	}

	@Override
	public void handle(Context context) {
		FuelMapper.setFuelMap(items);
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeVarInt(items.size());
		for (Item item : items) {
			buffer.writeRegistryId(item);
		}
	}

	public static SyncFuelMapperPKT decode(PacketBuffer buffer) {
		int size = buffer.readVarInt();
		List<Item> items = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			items.add(buffer.readRegistryId());
		}
		return new SyncFuelMapperPKT(items);
	}
}