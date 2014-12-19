package moze_intel.projecte.network.packets;

import net.minecraft.client.Minecraft;
import moze_intel.projecte.gameObjs.container.TransmuteTabletContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.gameObjs.items.TransmutationTablet;
import moze_intel.projecte.playerData.Transmutation;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

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
		Transmutation.setStoredEmc(Minecraft.getMinecraft().thePlayer.getCommandSenderName(), pkt.emc);
		
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
