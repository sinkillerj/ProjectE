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

public class RelaySyncPKT implements IMessage
{
	private int displayEmc;
	private double displayKleinEmc;
	private double displayRawEmc;
	private BlockPos pos;
	
	public RelaySyncPKT() {}
	
	public RelaySyncPKT(int displayEmc, double displayKleinEmc, double displayRawEmc, RelayMK1Tile tile)
	{
		this.displayEmc = displayEmc;
		this.displayKleinEmc = displayKleinEmc;
		this.displayRawEmc = displayRawEmc;
		this.pos = tile.getPos();
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		displayEmc = buf.readInt();
		displayKleinEmc = buf.readDouble();
		displayRawEmc = buf.readDouble();
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(displayEmc);
		buf.writeDouble(displayKleinEmc);
		buf.writeDouble(displayRawEmc);
		buf.writeLong(pos.toLong());
	}

	public static class Handler implements IMessageHandler<RelaySyncPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final RelaySyncPKT pkt, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> {
                TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.pos);

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
            });

			return null;
		}
	}
}
