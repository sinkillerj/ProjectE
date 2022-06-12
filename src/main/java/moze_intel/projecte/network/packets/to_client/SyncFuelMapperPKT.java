package moze_intel.projecte.network.packets.to_client;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public record SyncFuelMapperPKT(List<Item> items) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		FuelMapper.setFuelMap(items);
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(items.size());
		for (Item item : items) {
			buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, item);
		}
	}

	public static SyncFuelMapperPKT decode(FriendlyByteBuf buffer) {
		int size = buffer.readVarInt();
		List<Item> items = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			items.add(buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS));
		}
		return new SyncFuelMapperPKT(items);
	}
}