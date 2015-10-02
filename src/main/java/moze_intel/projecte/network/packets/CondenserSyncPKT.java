package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;

public class CondenserSyncPKT implements IMessage
{
	private int displayEmc;
	private int requiredEmc;
	private int x;
	private int y;
	private int z;
	
	public CondenserSyncPKT() {}
	
	public CondenserSyncPKT(int displayEmc, int requiredEmc, int x, int y, int z) 
	{
		this.displayEmc = displayEmc;
		this.requiredEmc = requiredEmc;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		displayEmc = buf.readInt();
		requiredEmc = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(displayEmc);
		buf.writeInt(requiredEmc);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public static class Handler implements IMessageHandler<CondenserSyncPKT, IMessage>
	{
		@Override
		public IMessage onMessage(CondenserSyncPKT pkt, MessageContext ctx)
		{
			TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.x, pkt.y, pkt.z);

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

			return null;
		}
	}
}
