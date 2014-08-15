package moze_intel.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.MozeCore;
import moze_intel.gameObjs.tiles.RelayMK1Tile;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class RelaySyncPKT implements IMessage, IMessageHandler<RelaySyncPKT, IMessage>
{
	private int displayEmc;
	private int x;
	private int y;
	private int z;
	
	public RelaySyncPKT() {}
	
	public RelaySyncPKT(int displayEmc, int x, int y, int z) 
	{
		this.displayEmc = displayEmc;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public IMessage onMessage(RelaySyncPKT pkt, MessageContext ctx) 
	{
		TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.x, pkt.y, pkt.z);
		
		if (tile == null)
		{
			MozeCore.logger.logFatal("NULL tile entity reference in Relay sync packet! Please report to dev!");
		}
		else
		{
			((RelayMK1Tile) tile).displayEmc = pkt.displayEmc;
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		displayEmc = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(displayEmc);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
}
