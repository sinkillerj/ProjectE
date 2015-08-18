package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CollectorSyncPKT implements IMessage
{
	private int displayEmc;
	private double displayKleinCharge;
	private BlockPos pos;
	
	public CollectorSyncPKT() {}
	
	public CollectorSyncPKT(int displayEmc, double displayKleinCharge, CollectorMK1Tile tile)
	{
		this.displayEmc = displayEmc;
		this.displayKleinCharge = displayKleinCharge;
		this.pos = tile.getPos();
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		displayEmc = buf.readInt();
		pos = BlockPos.fromLong(buf.readLong());
		displayKleinCharge = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(displayEmc);
		buf.writeLong(pos.toLong());
		buf.writeDouble(displayKleinCharge);
	}

	public static class Handler implements IMessageHandler<CollectorSyncPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final CollectorSyncPKT pkt, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.pos);

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
				}
			});

			return null;
		}
	}
}
