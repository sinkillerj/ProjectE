package moze_intel.network.packets;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import moze_intel.MozeCore;
import moze_intel.utils.PlayerBagInventory;
import moze_intel.utils.PlayerKnowledge;
import net.minecraft.client.Minecraft;
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
		PlayerBagInventory props = (PlayerBagInventory) Minecraft.getMinecraft().thePlayer.getExtendedProperties(PlayerBagInventory.EXT_PROP_NAME);
		
		if (props == null)
		{
			MozeCore.logger.logFatal("Exception during player knowledge sync: no knowledge found! Please report to dev!");
		}
		else
		{
			props.loadNBTData(message.nbt);
		}
		
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
