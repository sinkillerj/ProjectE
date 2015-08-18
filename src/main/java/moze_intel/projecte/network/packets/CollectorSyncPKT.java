package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

public class CollectorSyncPKT implements IMessage
{
	private int displayEmc;
	private double displayKleinCharge;
	private int x;
	private int y;
	private int z;
	
	public CollectorSyncPKT() {}
	
	public CollectorSyncPKT(int displayEmc, double displayKleinCharge, int x, int y, int z)
	{
		this.displayEmc = displayEmc;
		this.displayKleinCharge = displayKleinCharge;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		displayEmc = buf.readInt();
		displayKleinCharge = buf.readDouble();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(displayEmc);
		buf.writeDouble(displayKleinCharge);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public static class Handler implements IMessageHandler<CollectorSyncPKT, IMessage>
	{
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
				collector.displayItemCharge = pkt.displayKleinCharge;
			}

			return null;
		}
	}
}
