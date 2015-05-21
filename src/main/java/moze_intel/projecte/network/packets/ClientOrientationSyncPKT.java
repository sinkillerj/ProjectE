package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.TileEmcDirection;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

// TODO 1.8 EnumFacing
public class ClientOrientationSyncPKT implements IMessage, IMessageHandler<ClientOrientationSyncPKT, IMessage> 
{
	private int orientation;
	private BlockPos pos;
	
	public ClientOrientationSyncPKT() {}
	
	public ClientOrientationSyncPKT(TileEntity tile, int orientation) 
	{
		this.orientation = orientation;
		this.pos = tile.getPos();
	}

	@Override
	public IMessage onMessage(ClientOrientationSyncPKT pkt, MessageContext ctx) 
	{
		TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.pos);
		
		if (tile instanceof TileEmcDirection)
		{
			((TileEmcDirection) tile).setOrientation(pkt.orientation);
		}
		else
		{
			PELogger.logFatal("Couldn't find Tile Entity in passed in packet! Please report to dev!");
			PELogger.logFatal("Coordinates: " + pos.toString());
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		orientation = buf.readInt();
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(orientation);
		buf.writeLong(pos.toLong());
	}
}
