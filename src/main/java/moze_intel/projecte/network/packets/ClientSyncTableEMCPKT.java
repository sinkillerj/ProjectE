package moze_intel.projecte.network.packets;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.playerData.Transmutation;

public class ClientSyncTableEMCPKT implements IMessage, IMessageHandler<ClientSyncTableEMCPKT, IMessage>
{
	private double emc;
	
	public ClientSyncTableEMCPKT() {}
	
	public ClientSyncTableEMCPKT(double emc)
	{
		this.emc = emc;
	}

	@Override
	public IMessage onMessage(ClientSyncTableEMCPKT pkt, MessageContext ctx) 
	{
		Transmutation.setEmc(FMLClientHandler.instance().getClientPlayerEntity(), pkt.emc);
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		emc = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeDouble(emc);
	}
}
