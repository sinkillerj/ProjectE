package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class CollectorSyncPKT implements IMessage, IMessageHandler<CollectorSyncPKT, IMessage>
{
	private int displayEmc;
	private int displayKleinCharge;
	private int x;
	private int y;
	private int z;
	
	public CollectorSyncPKT() {}
	
	public CollectorSyncPKT(int displayEmc, int displayKleinCharge, int x, int y, int z) 
	{
		this.displayEmc = displayEmc;
		this.displayKleinCharge = displayKleinCharge;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public IMessage onMessage(CollectorSyncPKT pkt, MessageContext ctx) 
	{
		TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.x, pkt.y, pkt.z);
		
		if (tile == null)
		{
			PELogger.logFatal("NULL tile entity reference in Collector sync packet! Please report to dev!");
		}
		else
		{
			CollectorMK1Tile collector = (CollectorMK1Tile) tile;
			collector.displayEmc = pkt.displayEmc;
			collector.displayKleinCharge = pkt.displayKleinCharge;
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		displayEmc = buf.readInt();
		displayKleinCharge = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(displayEmc);
		buf.writeInt(displayKleinCharge);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
}
