package moze_intel.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.MozeCore;
import moze_intel.utils.PlayerKnowledge;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientKnowledgeSyncPKT implements IMessage, IMessageHandler<ClientKnowledgeSyncPKT, IMessage>
{
	private NBTTagCompound nbt;
	
	public ClientKnowledgeSyncPKT() {}
	
	public ClientKnowledgeSyncPKT(NBTTagCompound nbt) 
	{
		this.nbt = nbt;
	}
	
	@Override
	public IMessage onMessage(ClientKnowledgeSyncPKT message, MessageContext ctx) 
	{
		PlayerKnowledge props = (PlayerKnowledge) Minecraft.getMinecraft().thePlayer.getExtendedProperties(PlayerKnowledge.EXT_PROP_NAME);
		
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
