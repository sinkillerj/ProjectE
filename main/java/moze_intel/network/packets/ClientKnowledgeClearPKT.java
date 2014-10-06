package moze_intel.network.packets;

import moze_intel.playerData.TransmutationKnowledge;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientKnowledgeClearPKT implements IMessage, IMessageHandler<ClientKnowledgeClearPKT, IMessage> 
{
	private String username;
	
	public ClientKnowledgeClearPKT() {}
	
	public ClientKnowledgeClearPKT(String username) 
	{
		this.username = username;
	}
	
	@Override
	public IMessage onMessage(ClientKnowledgeClearPKT pkt, MessageContext ctx) 
	{
		TransmutationKnowledge.clearKnowledge(pkt.username);
		
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
