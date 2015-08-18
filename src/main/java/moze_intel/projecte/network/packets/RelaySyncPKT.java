package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

public class RelaySyncPKT implements IMessage
{
	private int displayEmc;
	private double displayKleinEmc;
	private double displayRawEmc;
	private int x;
	private int y;
	private int z;
	
	public RelaySyncPKT() {}
	
	public RelaySyncPKT(int displayEmc, double displayKleinEmc, double displayRawEmc, int x, int y, int z)
	{
		this.displayEmc = displayEmc;
		this.displayKleinEmc = displayKleinEmc;
		this.displayRawEmc = displayRawEmc;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		displayEmc = buf.readInt();
		displayKleinEmc = buf.readDouble();
		displayRawEmc = buf.readDouble();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(displayEmc);
		buf.writeDouble(displayKleinEmc);
		buf.writeDouble(displayRawEmc);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public static class Handler implements IMessageHandler<RelaySyncPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final RelaySyncPKT pkt, MessageContext ctx)
		{
			TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.x, pkt.y, pkt.z);

			if (tile == null)
			{
				PELogger.logFatal("NULL tile entity reference in Relay sync packet! Please report to dev!");
			}
			else
			{
				RelayMK1Tile relay = (RelayMK1Tile) tile;
				relay.displayEmc = pkt.displayEmc;
				relay.displayChargingEmc = pkt.displayKleinEmc;
				relay.displayRawEmc = pkt.displayRawEmc;
			}

			return null;
		}
	}
}
