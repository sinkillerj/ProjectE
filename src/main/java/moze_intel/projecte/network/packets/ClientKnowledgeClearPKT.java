package moze_intel.projecte.network.packets;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.playerData.Transmutation;

public class ClientKnowledgeClearPKT implements IMessage, IMessageHandler<ClientKnowledgeClearPKT, IMessage> 
{
	public ClientKnowledgeClearPKT() {}

	@Override
	public IMessage onMessage(ClientKnowledgeClearPKT pkt, MessageContext ctx) 
	{
		Transmutation.newClearKnowledge(FMLClientHandler.instance().getClientPlayerEntity());
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}
}
