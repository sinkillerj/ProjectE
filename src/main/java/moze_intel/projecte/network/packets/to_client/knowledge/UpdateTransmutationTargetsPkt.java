package moze_intel.projecte.network.packets.to_client.knowledge;

import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class UpdateTransmutationTargetsPkt implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.containerMenu instanceof TransmutationContainer container) {
			container.transmutationInventory.updateClientTargets();
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
	}

	public static UpdateTransmutationTargetsPkt decode(FriendlyByteBuf buffer) {
		return new UpdateTransmutationTargetsPkt();
	}
}