package moze_intel.projecte.network.packets;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;

import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.utils.PELogger;

import java.util.List;

public class SearchUpdatePKT implements IMessage
{
	public SearchUpdatePKT() {}

	public boolean applyImmediately = false;
	public List<ItemStack> outslots;
	public SearchUpdatePKT(List<ItemStack> outslots, boolean applyImmediately)
	{
		this.applyImmediately = applyImmediately;
		this.outslots = outslots;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		applyImmediately = buf.readBoolean();
		List<ItemStack> l = Lists.newArrayList();
		for (int i = 0; i < 16; i++) {
			l.add(ByteBufUtils.readItemStack(buf));
		}
		this.outslots = l;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(applyImmediately);
		for (int i = 0; i < 16; i++) {
			ByteBufUtils.writeItemStack(buf, outslots.get(i));
		}
	}

	public static class Handler implements IMessageHandler<SearchUpdatePKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SearchUpdatePKT pkt, final MessageContext ctx)
		{
			if (ctx.getServerHandler().playerEntity.openContainer instanceof TransmutationContainer)
			{
				TransmutationContainer container = ((TransmutationContainer) ctx.getServerHandler().playerEntity.openContainer);
				if (pkt.applyImmediately)
				{
					container.transmutationInventory.writeIntoOutputSlots(pkt.outslots);
					PELogger.logFatal("Wrote Output Slots from UpdatePacket immediately");
				}
				else
				{
					try
					{
						container.transmutationInventory.serverOutputSlotUpdates.put(pkt.outslots);
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					PELogger.logFatal("Got Output Slots from UpdatePacket... Size: " + container.transmutationInventory.serverOutputSlotUpdates.size());
				}
			}

			return null;
		}
	}
}
