package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.gameObjs.container.PEContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

// Version of SWindowPropertyPacket that does not truncate the `value` arg to a short
public record UpdateWindowIntPKT(short windowId, short propId, int propVal) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.containerMenu instanceof PEContainer container && player.containerMenu.containerId == windowId) {
			container.updateProgressBarInt(propId, propVal);
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