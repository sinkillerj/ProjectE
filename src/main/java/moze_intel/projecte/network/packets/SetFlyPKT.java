package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class SetFlyPKT implements IMessage
{
	private boolean flag;

	public SetFlyPKT() {}

	public SetFlyPKT(boolean value)
	{
		flag = value;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		flag = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(flag);
	}

	public static class Handler implements IMessageHandler<SetFlyPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SetFlyPKT message, MessageContext ctx)
		{
			Minecraft.getMinecraft().thePlayer.capabilities.allowFlying = message.flag;

			if (!message.flag)
			{
				Minecraft.getMinecraft().thePlayer.capabilities.isFlying = false;
			}

			return null;
		}
	}
}

