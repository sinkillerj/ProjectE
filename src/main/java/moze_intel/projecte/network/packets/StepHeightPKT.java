package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class StepHeightPKT implements IMessage
{
	private float value;

	public StepHeightPKT() {}

	public StepHeightPKT(float value)
	{
		this.value = value;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		value = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeFloat(value);
	}

	public static class Handler implements IMessageHandler<StepHeightPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final StepHeightPKT message, MessageContext ctx)
		{
			Minecraft.getMinecraft().thePlayer.stepHeight = message.value;
			return null;
		}
	}
}
