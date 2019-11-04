package moze_intel.projecte.network.packets;

import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.container.PEContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

// Version of SWindowPropertyPacket that supports long values
public class UpdateWindowLongPKT {

	private final short windowId;
	private final short propId;
	private final long propVal;

	public UpdateWindowLongPKT(short windowId, short propId, long propVal) {
		this.windowId = windowId;
		this.propId = propId;
		this.propVal = propVal;
	}

	public static void encode(UpdateWindowLongPKT msg, PacketBuffer buf) {
		buf.writeShort(msg.windowId);
		buf.writeShort(msg.propId);
		buf.writeLong(msg.propVal);
	}

	public static UpdateWindowLongPKT decode(PacketBuffer buf) {
		return new UpdateWindowLongPKT(buf.readShort(), buf.readShort(), buf.readLong());
	}

	public static class Handler {

		public static void handle(final UpdateWindowLongPKT msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> handleClient(msg));
			ctx.get().setPacketHandled(true);
		}

		//Needed to make the server be able to resolve registering the packet
		@OnlyIn(Dist.CLIENT)
		private static void handleClient(final UpdateWindowLongPKT msg) {
			PlayerEntity player = Minecraft.getInstance().player;
			if (player.openContainer instanceof PEContainer && player.openContainer.windowId == msg.windowId) {
				((PEContainer) player.openContainer).updateProgressBarLong(msg.propId, msg.propVal);
			}
		}
	}
}