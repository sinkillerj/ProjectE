package moze_intel.projecte.network.packets;

import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class StepHeightPKT {

	private final float value;

	public StepHeightPKT(float value) {
		this.value = value;
	}

	public static void encode(StepHeightPKT msg, PacketBuffer buf) {
		buf.writeFloat(msg.value);
	}

	public static StepHeightPKT decode(PacketBuffer buf) {
		return new StepHeightPKT(buf.readFloat());
	}

	public static class Handler {

		public static void handle(final StepHeightPKT message, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> Minecraft.getInstance().player.stepHeight = message.value);
			ctx.get().setPacketHandled(true);
		}
	}
}