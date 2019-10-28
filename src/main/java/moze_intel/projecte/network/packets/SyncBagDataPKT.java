package moze_intel.projecte.network.packets;

import java.util.function.Supplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncBagDataPKT {

	private final CompoundNBT nbt;

	public SyncBagDataPKT(CompoundNBT nbt) {
		this.nbt = nbt;
	}

	public static void encode(SyncBagDataPKT msg, PacketBuffer buf) {
		buf.writeCompoundTag(msg.nbt);
	}

	public static SyncBagDataPKT decode(PacketBuffer buf) {
		return new SyncBagDataPKT(buf.readCompoundTag());
	}

	public static class Handler {

		public static void handle(final SyncBagDataPKT message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				Minecraft.getInstance().player.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY)
						.ifPresent(cap -> cap.deserializeNBT(message.nbt));
				PECore.debugLog("** RECEIVED BAGS CLIENTSIDE **");
			});
			ctx.get().setPacketHandled(true);
		}
	}
}