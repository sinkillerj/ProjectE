package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.nbt.NBTTagCompound;

public class TransmutationSyncPKT implements IMessage
{
	private NBTTagCompound nbt;

	public TransmutationSyncPKT() {}

	public TransmutationSyncPKT(NBTTagCompound nbt)
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

	public static class Handler implements IMessageHandler<TransmutationSyncPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final TransmutationSyncPKT message, MessageContext ctx)
		{
			PECore.proxy.getClientTransmutationProps().readFromPacket(message.nbt);
			PELogger.logDebug("** RECEIVED TRANSMUTATION DATA CLIENTSIDE **");

			return null;
		}
	}
}
