package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.gameObjs.container.PEContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

// Version of SWindowPropertyPacket that supports long values
public class UpdateWindowLongPKT implements IPEPacket {

	private final short windowId;
	private final short propId;
	private final long propVal;

	public UpdateWindowLongPKT(short windowId, short propId, long propVal) {
		this.windowId = windowId;
		this.propId = propId;
		this.propVal = propVal;
	}

	@Override
	public void handle(Context context) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null && player.openContainer instanceof PEContainer && player.openContainer.windowId == windowId) {
			((PEContainer) player.openContainer).updateProgressBarLong(propId, propVal);
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeShort(windowId);
		buffer.writeShort(propId);
		buffer.writeLong(propVal);
	}

	public static UpdateWindowLongPKT decode(PacketBuffer buffer) {
		return new UpdateWindowLongPKT(buffer.readShort(), buffer.readShort(), buffer.readLong());
	}
}