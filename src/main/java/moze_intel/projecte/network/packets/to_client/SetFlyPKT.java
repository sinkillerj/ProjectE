package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record SetFlyPKT(boolean allowFlying, boolean isFlying) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.getAbilities().mayfly = allowFlying;
			Minecraft.getInstance().player.getAbilities().flying = isFlying;
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBoolean(allowFlying);
		buffer.writeBoolean(isFlying);
	}

	public static SetFlyPKT decode(FriendlyByteBuf buffer) {
		return new SetFlyPKT(buffer.readBoolean(), buffer.readBoolean());
	}
}