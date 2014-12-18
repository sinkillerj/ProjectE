package sinkillerj.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import sinkillerj.projecte.gameObjs.tiles.TileEmc;
import sinkillerj.projecte.utils.Coordinates;
import sinkillerj.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientTableSyncPKT implements IMessage, IMessageHandler<ClientTableSyncPKT, IMessage>
{
	private double emc;
	private int x;
	private int y;
	private int z;
	
	public ClientTableSyncPKT() {}
	
	public ClientTableSyncPKT(double emc, int x, int y, int z) 
	{
		this.emc = emc;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public IMessage onMessage(ClientTableSyncPKT pkt, MessageContext ctx) 
	{
		TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.x, pkt.y, pkt.z);
		
		if (tile == null)
		{
			PELogger.logFatal("NULL transmutation-tile reference! Please report to dev!");
			PELogger.logFatal("Coords: "+new Coordinates(pkt.x, pkt.y, pkt.z));
		}
		else if (tile instanceof TileEmc)
		{
			((TileEmc) tile).setEmcValue(pkt.emc);
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		emc = buf.readDouble();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeDouble(emc);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
}
