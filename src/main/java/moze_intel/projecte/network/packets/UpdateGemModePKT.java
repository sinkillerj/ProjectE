package moze_intel.projecte.network.packets;

import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.utils.Constants;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateGemModePKT {

	private final boolean mode;

	public UpdateGemModePKT(boolean mode) {
		this.mode = mode;
	}

	public static void encode(UpdateGemModePKT msg, PacketBuffer buf) {
		buf.writeBoolean(msg.mode);
	}

	public static UpdateGemModePKT decode(PacketBuffer buf) {
		return new UpdateGemModePKT(buf.readBoolean());
	}

	public static class Handler {

		public static void handle(final UpdateGemModePKT pkt, final Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				ItemStack stack = ctx.get().getSender().getHeldItem(Hand.MAIN_HAND);
				if (stack.isEmpty()) {
					stack = ctx.get().getSender().getHeldItem(Hand.OFF_HAND);
				}
				if (!stack.isEmpty() && stack.getItem() instanceof GemEternalDensity) {
					//Note: Void Ring extends gem of eternal density so we only need to check if it is an instance of the base class
					stack.getTag().putBoolean(Constants.NBT_KEY_GEM_WHITELIST, pkt.mode);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}