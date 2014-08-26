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
	private int displayKleinEmc;
	private int displayRawEmc;
	private int x;
	private int y;
	private int z;
	
	public RelaySyncPKT() {}
	
	public RelaySyncPKT(int displayEmc, int displayKleinEmc, int displayRawEmc, int x, int y, int z) 
	{
		this.displayEmc = displayEmc;
		this.displayKleinEmc = displayKleinEmc;
		this.displayRawEmc = displayRawEmc;
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
			RelayMK1Tile relay = (RelayMK1Tile) tile;
			relay.displayEmc = pkt.displayEmc;
			relay.displayKleinEmc = pkt.displayKleinEmc;
			relay.displayRawEmc = pkt.displayRawEmc;
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		displayEmc = buf.readInt();
		displayKleinEmc = buf.readInt();
		displayRawEmc = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(displayEmc);
		buf.writeInt(displayKleinEmc);
		buf.writeInt(displayRawEmc);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}
}
