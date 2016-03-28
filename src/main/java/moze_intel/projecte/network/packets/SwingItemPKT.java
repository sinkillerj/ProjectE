package moze_intel.projecte.network.packets;

import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class SwingItemPKT implements IMessage
{
	private EnumHand hand;

	public SwingItemPKT() { this(EnumHand.MAIN_HAND); }

	public SwingItemPKT(EnumHand hand)
	{
		this.hand = hand;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		hand = EnumHand.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(hand.ordinal());
	}

	public static class Handler implements IMessageHandler<SwingItemPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SwingItemPKT message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Minecraft.getMinecraft().thePlayer.swingArm(message.hand);
				}
			});

			return null;
		}
	}
}
