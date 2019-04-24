package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.emc.EMCMapper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncCovalencePKT implements IMessage
{
	private double covalenceLoss;
	private boolean covalenceLossRounding;

	public SyncCovalencePKT() {}

	public SyncCovalencePKT(double value, boolean rounding)
	{
		covalenceLoss = value;
		covalenceLossRounding = rounding;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		covalenceLoss = buf.readDouble();
		covalenceLossRounding = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeDouble(covalenceLoss);
		buf.writeBoolean(covalenceLossRounding);
	}

	public static class Handler implements IMessageHandler<SyncCovalencePKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SyncCovalencePKT message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EMCMapper.covalenceLoss = message.covalenceLoss;
					EMCMapper.covalenceLossRounding = message.covalenceLossRounding;
				}
			});

			return null;
		}
	}
}
