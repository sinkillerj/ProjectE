package moze_intel.projecte.network.packets;

import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.container.PEContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

// Version of SWindowPropertyPacket that does not truncate the `value` arg to a short
public class UpdateWindowIntPKT {

	private final short windowId;
	private final short propId;
	private final int propVal;

	public UpdateWindowIntPKT(short windowId, short propId, int propVal) {
		this.windowId = windowId;
		this.propId = propId;
		this.propVal = propVal;
	}

	public static void encode(UpdateWindowIntPKT msg, PacketBuffer buf) {
		buf.writeShort(msg.windowId);
		buf.writeShort(msg.propId);
		buf.writeVarInt(msg.propVal);
	}

	public static UpdateWindowIntPKT decode(PacketBuffer buf) {
		return new UpdateWindowIntPKT(buf.readShort(), buf.readShort(), buf.readVarInt());
	}

	public static class Handler {

		public static void handle(final UpdateWindowIntPKT msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> handleClient(msg));
			ctx.get().setPacketHandled(true);
		}

		//Needed to make the server be able to resolve registering the packet
		@OnlyIn(Dist.CLIENT)
		private static void handleClient(final UpdateWindowIntPKT msg) {
			PlayerEntity player = Minecraft.getInstance().player;
			if (player.openContainer instanceof PEContainer && player.openContainer.windowId == msg.windowId) {
				((PEContainer) player.openContainer).updateProgressBarInt(msg.propId, msg.propVal);
			}
		}
	}
}