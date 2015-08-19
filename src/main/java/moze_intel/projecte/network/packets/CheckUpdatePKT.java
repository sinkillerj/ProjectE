package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.network.ThreadCheckUpdate;

public class CheckUpdatePKT implements IMessage
{
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	public static class Handler implements IMessageHandler<CheckUpdatePKT, IMessage>
	{
		@Override
		public IMessage onMessage(CheckUpdatePKT message, MessageContext ctx)
		{
			if (!ThreadCheckUpdate.hasRunClient())
			{
				new ThreadCheckUpdate(false).start();
			}

			return null;
		}
	}
}
