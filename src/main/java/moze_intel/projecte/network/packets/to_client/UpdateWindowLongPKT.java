package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.gameObjs.container.PEContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

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
	public void handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.containerMenu instanceof PEContainer && player.containerMenu.containerId == windowId) {
			((PEContainer) player.containerMenu).updateProgressBarLong(propId, propVal);
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeShort(windowId);
		buffer.writeShort(propId);
		buffer.writeLong(propVal);
	}

	public static UpdateWindowLongPKT decode(FriendlyByteBuf buffer) {
		return new UpdateWindowLongPKT(buffer.readShort(), buffer.readShort(), buffer.readLong());
	}
}