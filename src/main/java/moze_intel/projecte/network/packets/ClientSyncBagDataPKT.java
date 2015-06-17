package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.nbt.NBTTagCompound;

public class ClientSyncBagDataPKT implements IMessage, IMessageHandler<ClientSyncBagDataPKT, IMessage>
{
	private NBTTagCompound nbt;
	
	public ClientSyncBagDataPKT() {}
	
	public ClientSyncBagDataPKT(NBTTagCompound nbt) 
	{
		this.nbt = nbt;
	}

	@Override
	public IMessage onMessage(ClientSyncBagDataPKT message, MessageContext ctx)
	{
		PECore.proxy.getClientBagProps().readFromPacket(message.nbt);
		PELogger.logInfo("** RECEIVED BAGS CLIENTSIDE **");
		return null;
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
}
