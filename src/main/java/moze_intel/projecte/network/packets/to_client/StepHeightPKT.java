package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record StepHeightPKT(float value) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.maxUpStep = value;
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeFloat(value);
	}

	public static StepHeightPKT decode(FriendlyByteBuf buffer) {
		return new StepHeightPKT(buffer.readFloat());
	}
}