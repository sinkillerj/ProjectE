package moze_intel.projecte.network.packets.to_client.knowledge;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record KnowledgeSyncPKT(CompoundTag nbt) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("knowledge_sync");

	public KnowledgeSyncPKT(FriendlyByteBuf buffer) {
		this(buffer.readNbt());
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(PlayPayloadContext context) {
		context.player().ifPresent(player -> {
			player.getData(PEAttachmentTypes.KNOWLEDGE).deserializeNBT(nbt);
			if (player.containerMenu instanceof TransmutationContainer container) {
				container.transmutationInventory.updateClientTargets();
			}
		});
		PECore.debugLog("** RECEIVED TRANSMUTATION DATA CLIENTSIDE **");
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeNbt(nbt);
	}
}