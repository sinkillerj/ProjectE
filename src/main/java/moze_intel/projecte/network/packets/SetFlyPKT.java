package moze_intel.projecte.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SetFlyPKT {
	private final boolean flag;

	public SetFlyPKT(boolean value)
	{
		flag = value;
	}

	public static void encode(SetFlyPKT msg, PacketBuffer buf)
	{
		buf.writeBoolean(msg.flag);
	}

	public static SetFlyPKT decode(PacketBuffer buf)
	{
		return new SetFlyPKT(buf.readBoolean());
	}

	public static class Handler
	{
		public static void handle(final SetFlyPKT message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> {
				Minecraft.getInstance().player.abilities.allowFlying = message.flag;

				if (!message.flag)
				{
					Minecraft.getInstance().player.abilities.isFlying = false;
				}
			});
		}
	}
}

