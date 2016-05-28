package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class SearchUpdatePKT implements IMessage
{
	public SearchUpdatePKT() {}

	public int slot;
	public ItemStack itemStack;
	public SearchUpdatePKT(int slot, ItemStack itemStack)
	{
		this.slot = slot;
		this.itemStack = itemStack != null ? itemStack.copy() : null;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		slot = buf.readInt();
		itemStack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(slot);
		ByteBufUtils.writeItemStack(buf, itemStack);
	}

	public static class Handler implements IMessageHandler<SearchUpdatePKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SearchUpdatePKT pkt, final MessageContext ctx)
		{
			ctx.getServerHandler().playerEntity.mcServer.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					if (ctx.getServerHandler().playerEntity.openContainer instanceof TransmutationContainer)
					{
						TransmutationContainer container = ((TransmutationContainer) ctx.getServerHandler().playerEntity.openContainer);
						container.transmutationInventory.writeIntoOutputSlot(pkt.slot, pkt.itemStack);
					}
				}
			});
			return null;
		}
	}
}
