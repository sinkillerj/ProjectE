package moze_intel.projecte.network.packets.to_client;

import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncFuelMapperPKT(List<Item> items) implements IPEPacket<IPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("sync_fuel_mapper");

	public SyncFuelMapperPKT(FriendlyByteBuf buffer) {
		this(buffer.readList(buf -> buf.readById(BuiltInRegistries.ITEM)));
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(IPayloadContext context) {
		FuelMapper.setFuelMap(items);
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeCollection(items, (buf, item) -> buf.writeId(BuiltInRegistries.ITEM, item));
	}
}