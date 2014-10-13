package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.container.TransmuteTabletContainer;
import moze_intel.projecte.gameObjs.tiles.TileEmc;
import moze_intel.projecte.utils.Coordinates;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PortableTableSyncPKT implements IMessage, IMessageHandler<PortableTableSyncPKT, IMessage>
{
	private double emc;
	
	public PortableTableSyncPKT() {}
	
	public PortableTableSyncPKT(double emc) 
	{
		this.emc = emc;
	}
	
	@Override
	public IMessage onMessage(PortableTableSyncPKT pkt, MessageContext ctx) 
	{
		Container cont = Minecraft.getMinecraft().thePlayer.openContainer;
		
		if (cont instanceof TransmuteTabletContainer)
		{
			((TransmuteTabletContainer) cont).table.emc = pkt.emc;
		}
		
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
