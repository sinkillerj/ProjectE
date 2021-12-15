package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CooldownResetPKT implements IPEPacket {

	@Override
	public void handle(Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.resetAttackStrengthTicker();
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
	}

	public static CooldownResetPKT decode(PacketBuffer buffer) {
		return new CooldownResetPKT();
	}
}