package moze_intel.projecte.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CooldownResetPKT {

	public static void encode(CooldownResetPKT pkt, PacketBuffer buf) {}

	public static CooldownResetPKT decode(PacketBuffer buf) {
		return new CooldownResetPKT();
	}

	public static class Handler
	{
		public static void handle(CooldownResetPKT message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> Minecraft.getInstance().player.resetCooldown());
			ctx.get().setPacketHandled(true);
		}
	}

}
