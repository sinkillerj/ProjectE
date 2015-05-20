package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class RelaySyncPKT implements IMessage, IMessageHandler<RelaySyncPKT, IMessage>
{
	private int displayEmc;
	private int displayKleinEmc;
	private int displayRawEmc;
	private BlockPos pos;
	
	public RelaySyncPKT() {}
	
	public RelaySyncPKT(int displayEmc, int displayKleinEmc, int displayRawEmc, RelayMK1Tile tile)
	{
		this.displayEmc = displayEmc;
		this.displayKleinEmc = displayKleinEmc;
		this.displayRawEmc = displayRawEmc;
		this.pos = tile.getPos();
	}
	
	@Override
	public IMessage onMessage(RelaySyncPKT pkt, MessageContext ctx) 
	{
		TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.pos);
		
		if (tile == null)
		{
			PELogger.logFatal("NULL tile entity reference in Relay sync packet! Please report to dev!");
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
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(displayEmc);
		buf.writeInt(displayKleinEmc);
		buf.writeInt(displayRawEmc);
		buf.writeLong(pos.toLong());
	}
}
