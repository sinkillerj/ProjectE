package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SyncBagDataPKT implements IPEPacket {

	private final CompoundNBT nbt;

	public SyncBagDataPKT(CompoundNBT nbt) {
		this.nbt = nbt;
	}

	@Override
	public void handle(Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY).ifPresent(cap -> cap.deserializeNBT(nbt));
		}
		PECore.debugLog("** RECEIVED BAGS CLIENTSIDE **");
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeNbt(nbt);
	}

	public static SyncBagDataPKT decode(PacketBuffer buffer) {
		return new SyncBagDataPKT(buffer.readNbt());
	}
}