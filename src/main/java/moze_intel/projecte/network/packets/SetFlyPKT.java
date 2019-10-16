package moze_intel.projecte.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SetFlyPKT {
	private boolean allowFlying;
	private boolean isFlying;

	public SetFlyPKT(boolean allowFlying, boolean isFlying)
	{
		this.allowFlying = allowFlying;
		this.isFlying = isFlying;
	}

	public static void encode(SetFlyPKT msg, PacketBuffer buf)
	{
		buf.writeBoolean(msg.allowFlying);
		buf.writeBoolean(msg.isFlying);
	}

	public static SetFlyPKT decode(PacketBuffer buf)
	{
		return new SetFlyPKT(buf.readBoolean(), buf.readBoolean());
	}

	public static class Handler
	{
		public static void handle(final SetFlyPKT message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> {
				Minecraft.getInstance().player.abilities.allowFlying = message.allowFlying;
				Minecraft.getInstance().player.abilities.isFlying = message.isFlying;
			});
			ctx.get().setPacketHandled(true);
		}
	}
}

