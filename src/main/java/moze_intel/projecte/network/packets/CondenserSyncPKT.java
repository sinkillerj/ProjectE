package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CondenserSyncPKT implements IMessage, IMessageHandler<CondenserSyncPKT, IMessage>
{
	private int displayEmc;
	private int requiredEmc;
	private BlockPos pos;
	
	public CondenserSyncPKT() {}
	
	public CondenserSyncPKT(int displayEmc, int requiredEmc, CondenserTile tile)
	{
		this.displayEmc = displayEmc;
		this.requiredEmc = requiredEmc;
		this.pos = tile.getPos();
	}
	
	@Override
	public IMessage onMessage(final CondenserSyncPKT pkt, MessageContext ctx)
	{
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			@Override
			public void run() {
				TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.pos);

				if (tile == null)
				{
					PELogger.logFatal("NULL tile entity reference in condenser update packet! Please report to dev!");
				}
				else
				{
					CondenserTile cond = (CondenserTile) tile;
					cond.displayEmc = pkt.displayEmc;
					cond.requiredEmc = pkt.requiredEmc;
				}
			}
		});

		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		displayEmc = buf.readInt();
		requiredEmc = buf.readInt();
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(displayEmc);
		buf.writeInt(requiredEmc);
		buf.writeLong(pos.toLong());
	}
}
