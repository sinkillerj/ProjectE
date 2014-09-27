package moze_intel.network.packets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.netty.buffer.ByteBuf;
import moze_intel.MozeCore;
import moze_intel.EMC.EMCMapper;
import moze_intel.config.FileHelper;
import moze_intel.playerData.TransmutationKnowledge;
import moze_intel.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class AddEmcPKT implements IMessage, IMessageHandler<AddEmcPKT, IMessage>
{
	private Object obj;
	private int emc;
	
	public AddEmcPKT() {}
	
	public AddEmcPKT(ItemStack stack, int value)
	{
		obj = stack;
		emc = value;
	}
	
	public AddEmcPKT(String odName, int value)
	{
		obj = odName;
		emc = value;
	}

	@Override
	public IMessage onMessage(AddEmcPKT pkt, MessageContext ctx) 
	{
		if (pkt.obj instanceof ItemStack)
		{
			FileHelper.addToFile((ItemStack) pkt.obj, pkt.emc);
		}
		else
		{
			for (ItemStack stack : Utils.getODItems((String) pkt.obj))
			{
				FileHelper.addToFile(stack, pkt.emc);
			}
		}
		
		EMCMapper.clearMaps();
		
		FileHelper.readUserData();
		
		EMCMapper.map();
		
		TransmutationKnowledge.loadCompleteKnowledge();
		
		MozeCore.pktHandler.sendToAll(new ClientSyncPKT());
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		if (buf.readBoolean())
		{
			obj = ByteBufUtils.readItemStack(buf);
		}
		else
		{
			obj = ByteBufUtils.readUTF8String(buf);
		}
		
		emc = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		
		if (obj instanceof ItemStack)
		{
			buf.writeBoolean(true);
			ByteBufUtils.writeItemStack(buf, (ItemStack) obj);
		}
		else
		{
			buf.writeBoolean(false);
			ByteBufUtils.writeUTF8String(buf, (String) obj);
		}
		
		buf.writeInt(emc);
	}
}
