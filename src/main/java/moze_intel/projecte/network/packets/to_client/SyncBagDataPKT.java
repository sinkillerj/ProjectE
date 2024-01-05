package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncBagDataPKT(CompoundTag nbt) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("sync_bag_data");

	public SyncBagDataPKT(FriendlyByteBuf buffer) {
		this(buffer.readNbt());
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(PlayPayloadContext context) {
		//TODO - 1.20.4: Test this
		context.player().ifPresent(player -> player.getData(PEAttachmentTypes.ALCHEMICAL_BAGS).deserializeNBT(nbt));
		PECore.debugLog("** RECEIVED BAGS CLIENTSIDE **");
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeNbt(nbt);
	}
}