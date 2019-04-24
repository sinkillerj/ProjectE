package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetFlyPKT implements IMessage
{
	private boolean allowFlying;
	private boolean isFlying;

	public SetFlyPKT() {}

	public SetFlyPKT(boolean allowFlying, boolean isFlying)
	{
		this.allowFlying = allowFlying;
		this.isFlying = isFlying;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		allowFlying = buf.readBoolean();
		isFlying = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(allowFlying);
		buf.writeBoolean(isFlying);
	}

	public static class Handler implements IMessageHandler<SetFlyPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SetFlyPKT message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Minecraft.getMinecraft().player.capabilities.allowFlying = message.allowFlying;
				Minecraft.getMinecraft().player.capabilities.isFlying = message.isFlying;
			});

			return null;
		}
	}
}

