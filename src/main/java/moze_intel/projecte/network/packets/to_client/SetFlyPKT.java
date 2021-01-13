package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SetFlyPKT implements IPEPacket {

	private final boolean allowFlying;
	private final boolean isFlying;

	public SetFlyPKT(boolean allowFlying, boolean isFlying) {
		this.allowFlying = allowFlying;
		this.isFlying = isFlying;
	}

	@Override
	public void handle(Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.abilities.allowFlying = allowFlying;
			Minecraft.getInstance().player.abilities.isFlying = isFlying;
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBoolean(allowFlying);
		buffer.writeBoolean(isFlying);
	}

	public static SetFlyPKT decode(PacketBuffer buffer) {
		return new SetFlyPKT(buffer.readBoolean(), buffer.readBoolean());
	}
}