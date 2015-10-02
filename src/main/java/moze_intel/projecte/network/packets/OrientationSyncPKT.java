package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.TileEmcDirection;
import moze_intel.projecte.utils.Coordinates;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

public class OrientationSyncPKT implements IMessage
{
	private int orientation;
	private int x;
	private int y;
	private int z;
	
	public OrientationSyncPKT() {}
	
	public OrientationSyncPKT(TileEntity tile, int orientation)
	{
		this.orientation = orientation;
		this.x = tile.xCoord;
		this.y = tile.yCoord;
		this.z = tile.zCoord;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		orientation = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(orientation);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public static class Handler implements IMessageHandler<OrientationSyncPKT, IMessage>
	{
		@Override
		public IMessage onMessage(OrientationSyncPKT pkt, MessageContext ctx)
		{
			TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.x, pkt.y, pkt.z);

			if (tile instanceof TileEmcDirection)
			{
				((TileEmcDirection) tile).setOrientation(pkt.orientation);
			}
			else
			{
				PELogger.logFatal("Couldn't find Tile Entity in passed in packet! Please report to dev!");
				PELogger.logFatal("Coordinates: " + new Coordinates(pkt.x, pkt.y, pkt.z));
			}

			return null;
		}
	}
}
