package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Minecraft.getMinecraft().player.stepHeight = message.value;
				}
			});
			return null;
		}
	}
}
