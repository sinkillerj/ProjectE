package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SyncBagDataPKT(CompoundTag nbt) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY).ifPresent(cap -> cap.deserializeNBT(nbt));
		}
		PECore.debugLog("** RECEIVED BAGS CLIENTSIDE **");
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt(nbt);
	}

	public static SyncBagDataPKT decode(FriendlyByteBuf buffer) {
		return new SyncBagDataPKT(buffer.readNbt());
	}
}