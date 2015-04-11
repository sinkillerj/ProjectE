package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.container.TransmuteContainer;
import moze_intel.projecte.gameObjs.container.TransmuteTabletContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
import net.minecraft.inventory.Container;

public class SearchUpdatePKT implements IMessage, IMessageHandler<SearchUpdatePKT, IMessage> 
{
	private String search;
	private int searchpage;

	public SearchUpdatePKT() {}

	public SearchUpdatePKT(String search, int page) 
	{
		this.search = search;
		this.searchpage = page;
	}

	@Override
	public IMessage onMessage(SearchUpdatePKT pkt, MessageContext ctx) 
	{
		Container cont = ctx.getServerHandler().playerEntity.openContainer;
		
		if (cont instanceof TransmuteContainer) 
		{
			TransmuteTile tile = ((TransmuteContainer) cont).tile;
			
			if (pkt.search != null)
			{
				tile.filter = pkt.search;
			}
			else
			{
				tile.filter = "";
			}

			tile.searchpage = pkt.searchpage;
			
			tile.updateOutputs();
		}
		else if (cont instanceof TransmuteTabletContainer)
		{
			TransmuteTabletInventory inv = ((TransmuteTabletContainer) cont).table;
			
			if (pkt.search != null)
			{
				inv.filter = pkt.search;
			}
			else
			{
				inv.filter = "";
			}

			inv.searchpage = pkt.searchpage;
			
			inv.updateOutputs();
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		search = ByteBufUtils.readUTF8String(buf);
		searchpage = ByteBufUtils.readVarShort(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeUTF8String(buf, search);
		ByteBufUtils.writeVarShort(buf, searchpage);
	}
}
