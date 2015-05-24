package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.container.TransmuteContainer;
import moze_intel.projecte.gameObjs.container.TransmuteTabletContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmuteTabletInventory;
import moze_intel.projecte.gameObjs.tiles.TransmuteTile;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
	public IMessage onMessage(final SearchUpdatePKT pkt, final MessageContext ctx)
	{
		ctx.getServerHandler().playerEntity.mcServer.addScheduledTask(new Runnable() {
			@Override
			public void run() {
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
			}
		});
		
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
