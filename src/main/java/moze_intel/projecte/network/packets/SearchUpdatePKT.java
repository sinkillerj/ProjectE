package moze_intel.projecte.network.packets;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import scala.actors.threadpool.BlockingQueue;

import moze_intel.projecte.gameObjs.container.TransmutationContainer;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class SearchUpdatePKT implements IMessage, IMessageHandler<SearchUpdatePKT, IMessage> 
{



	public SearchUpdatePKT() {}
	public List<ItemStack> outslots;
	public SearchUpdatePKT(List<ItemStack> outslots)
	{
		this.outslots = outslots;
	}

	@Override
	public IMessage onMessage(SearchUpdatePKT pkt, MessageContext ctx) 
	{
		if (ctx.getServerHandler().playerEntity.openContainer instanceof TransmutationContainer)
		{
			TransmutationContainer container = ((TransmutationContainer) ctx.getServerHandler().playerEntity.openContainer);
			try
			{
				container.transmutationInventory.serverOutputSlotUpdates.put(pkt.outslots);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		List<ItemStack> l = Lists.newArrayList();
		for (int i = 0; i < 16; i++) {
			l.add(ByteBufUtils.readItemStack(buf));
		}
		this.outslots = l;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		for (int i = 0; i < 16; i++) {
			ByteBufUtils.writeItemStack(buf, outslots.get(i));
		}
	}
}
