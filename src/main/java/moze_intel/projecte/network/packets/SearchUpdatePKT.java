package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
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
				if (ctx.getServerHandler().playerEntity.openContainer instanceof TransmutationContainer)
				{
					TransmutationContainer container = ((TransmutationContainer) ctx.getServerHandler().playerEntity.openContainer);

					if (pkt.search != null)
					{
						container.transmutationInventory.filter = pkt.search;
					}
					else
					{
						container.transmutationInventory.filter = "";
					}

					container.transmutationInventory.searchpage = pkt.searchpage;

					container.transmutationInventory.updateOutputs();
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
