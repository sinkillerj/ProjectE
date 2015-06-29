package moze_intel.projecte.network.packets;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.network.ThreadCheckUpdate;

public class CheckUpdatePKT implements IMessage, IMessageHandler<CheckUpdatePKT, IMessage>
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

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}
}
