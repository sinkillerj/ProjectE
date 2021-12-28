package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class CooldownResetPKT implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.resetAttackStrengthTicker();
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
	}

	public static CooldownResetPKT decode(FriendlyByteBuf buffer) {
		return new CooldownResetPKT();
	}
}