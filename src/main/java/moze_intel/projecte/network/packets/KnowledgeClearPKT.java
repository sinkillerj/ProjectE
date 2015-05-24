package moze_intel.projecte.network.packets;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.playerData.Transmutation;

public class KnowledgeClearPKT implements IMessage, IMessageHandler<KnowledgeClearPKT, IMessage>
{
	private String username;
	
	public KnowledgeClearPKT() {}
	
	public KnowledgeClearPKT(String username)
	{
		this.username = username;
	}
	
	@Override
	public IMessage onMessage(KnowledgeClearPKT pkt, MessageContext ctx)
	{
		Transmutation.clearKnowledge(pkt.username);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		username = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeUTF8String(buf, username);
	}
}
