package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.gameObjs.container.PEContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

// Version of SWindowPropertyPacket that does not truncate the `value` arg to a short
public class UpdateWindowIntPKT implements IPEPacket {

	private final short windowId;
	private final short propId;
	private final int propVal;

	public UpdateWindowIntPKT(short windowId, short propId, int propVal) {
		this.windowId = windowId;
		this.propId = propId;
		this.propVal = propVal;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.containerMenu instanceof PEContainer && player.containerMenu.containerId == windowId) {
			((PEContainer) player.containerMenu).updateProgressBarInt(propId, propVal);
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeShort(windowId);
		buffer.writeShort(propId);
		buffer.writeVarInt(propVal);
	}

	public static UpdateWindowIntPKT decode(FriendlyByteBuf buffer) {
		return new UpdateWindowIntPKT(buffer.readShort(), buffer.readShort(), buffer.readVarInt());
	}
}