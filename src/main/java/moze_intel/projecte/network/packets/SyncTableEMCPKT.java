package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.playerData.Transmutation;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncTableEMCPKT implements IMessage, IMessageHandler<SyncTableEMCPKT, IMessage>
{
	private double emc;
	
	public SyncTableEMCPKT() {}
	
	public SyncTableEMCPKT(double emc)
	{
		this.emc = emc;
	}

	@Override
	public IMessage onMessage(final SyncTableEMCPKT pkt, MessageContext ctx)
	{
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			@Override
			public void run() {
				Transmutation.setStoredEmc(Minecraft.getMinecraft().thePlayer.getCommandSenderName(), pkt.emc);
			}
		});
		
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
