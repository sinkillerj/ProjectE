package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.NBTWhitelist;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;

public class RequestTransmutationPKT implements IMessage
{
	private ItemStack stack;

	public RequestTransmutationPKT() {}

	public RequestTransmutationPKT(ItemStack stack)
	{
		this.stack = stack;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		stack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeItemStack(buf, stack);
	}

	public static class Handler implements IMessageHandler<RequestTransmutationPKT, IMessage>
	{
		@Override
		public IMessage onMessage(RequestTransmutationPKT message, MessageContext ctx)
		{
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			ItemStack request = message.stack.copy();
			if (!NBTWhitelist.shouldDupeWithNBT(request))
			{
				request.setTagCompound(null);
			}

			if (!(player.openContainer instanceof TransmutationContainer)
					|| !EMCHelper.doesItemHaveEmc(request)
					|| (player.inventory.getItemStack() != null
						&& (!ItemHelper.areItemStacksEqual(player.inventory.getItemStack(), request) || player.inventory.getItemStack().stackSize == player.inventory.getItemStack().getMaxStackSize()))
					|| !Transmutation.hasKnowledgeForStack(request, player)
				)
			{
				return null;
			}

			double avail = Transmutation.getEmc(player);
			double consume = EMCHelper.getEmcValue(request) * request.stackSize;
			if (avail > consume)
			{
				Transmutation.setEmc(player, avail - consume);
				((TransmutationContainer) player.openContainer).transmutationInventory.emc = Transmutation.getEmc(player);

				if (player.inventory.getItemStack() == null)
				{
					player.inventory.setItemStack(request);
				} else
				{
					player.inventory.getItemStack().stackSize += request.stackSize;
				}

				Transmutation.syncEmc(player);
				player.playerNetServerHandler.sendPacket(new S2FPacketSetSlot(-1, -1, player.inventory.getItemStack()));
			}

			return null;
		}
	}
}
