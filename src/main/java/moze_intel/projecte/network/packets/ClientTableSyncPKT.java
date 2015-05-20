package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.TileEmc;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientTableSyncPKT implements IMessage, IMessageHandler<ClientTableSyncPKT, IMessage>
{
	private double emc;
	private BlockPos pos;
	
	public ClientTableSyncPKT() {}
	
	public ClientTableSyncPKT(double emc, TileEmc tile)
	{
		this.emc = emc;
		this.pos = tile.getPos();
	}
	
	@Override
	public IMessage onMessage(ClientTableSyncPKT pkt, MessageContext ctx) 
	{
		TileEntity tile = Minecraft.getMinecraft().theWorld.getTileEntity(pkt.pos);
		
		if (tile == null)
		{
			PELogger.logFatal("NULL transmutation-tile reference! Please report to dev!");
			PELogger.logFatal("Coords: " + pkt.pos.toString());
		}
		else if (tile instanceof TileEmc)
		{
			((TileEmc) tile).setEmcValue(pkt.emc);
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		emc = buf.readDouble();
		pos = BlockPos.fromLong(buf.readLong());
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeDouble(emc);
		buf.writeLong(pos.toLong());
	}
}
