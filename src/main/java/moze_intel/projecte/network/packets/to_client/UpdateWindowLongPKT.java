package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.gameObjs.container.PEContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

// Version of SWindowPropertyPacket that supports long values
public record UpdateWindowLongPKT(short windowId, short propId, long propVal) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.containerMenu instanceof PEContainer container && player.containerMenu.containerId == windowId) {
			container.updateProgressBarLong(propId, propVal);
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