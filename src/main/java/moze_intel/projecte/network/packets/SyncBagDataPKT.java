package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.PECore;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncBagDataPKT implements IMessage
{
	private NBTTagCompound nbt;

	public SyncBagDataPKT() {}

	public SyncBagDataPKT(NBTTagCompound nbt)
	{
		this.nbt = nbt;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		nbt = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeTag(buf, nbt);
	}

	public static class Handler implements IMessageHandler<SyncBagDataPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SyncBagDataPKT message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					PECore.proxy.getClientBagProps().deserializeNBT(message.nbt);
					PECore.debugLog("** RECEIVED BAGS CLIENTSIDE **");
				}
			});

			return null;
		}
	}
}
