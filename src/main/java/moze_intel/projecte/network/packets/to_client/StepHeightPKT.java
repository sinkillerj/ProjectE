package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class StepHeightPKT implements IPEPacket {

	private final float value;

	public StepHeightPKT(float value) {
		this.value = value;
	}

	@Override
	public void handle(Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.stepHeight = value;
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeFloat(value);
	}

	public static StepHeightPKT decode(PacketBuffer buffer) {
		return new StepHeightPKT(buffer.readFloat());
	}
}