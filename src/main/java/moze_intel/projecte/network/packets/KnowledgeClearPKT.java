package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.PECore;

public class KnowledgeClearPKT implements IMessage
{
	public KnowledgeClearPKT() {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	public static class Handler implements IMessageHandler<KnowledgeClearPKT, IMessage>
	{
		@Override
		public IMessage onMessage(KnowledgeClearPKT pkt, MessageContext ctx)
		{
			PECore.proxy.clearClientKnowledge();
			return null;
		}
	}
}
