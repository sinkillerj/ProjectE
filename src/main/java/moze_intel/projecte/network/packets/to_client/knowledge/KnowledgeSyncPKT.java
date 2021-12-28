package moze_intel.projecte.network.packets.to_client.knowledge;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record KnowledgeSyncPKT(CompoundTag nbt) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(cap -> cap.deserializeNBT(nbt));
		}
		PECore.debugLog("** RECEIVED TRANSMUTATION DATA CLIENTSIDE **");
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt(nbt);
	}

	public static KnowledgeSyncPKT decode(FriendlyByteBuf buffer) {
		return new KnowledgeSyncPKT(buffer.readNbt());
	}
}