package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.PELogger;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientSyncPKT implements IMessage, IMessageHandler<ClientSyncPKT, IMessage>
{
	private static LinkedHashMap<SimpleStack, Integer> map;
	
	public ClientSyncPKT() {}

	@Override
	public IMessage onMessage(ClientSyncPKT message, MessageContext ctx)
	{
		PELogger.logInfo("Receiving EMC mapping from the server.");
		
		EMCMapper.emc.clear();
		EMCMapper.emc = (LinkedHashMap<SimpleStack, Integer>) message.map;
		
		Transmutation.loadCompleteKnowledge();
		FuelMapper.loadMap();
		
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		map = new LinkedHashMap();
		
		int size = buf.readInt();
		
		for (int i = 0; i < size; i++)
		{
			SimpleStack stack = new SimpleStack(buf.readInt(), buf.readInt(), buf.readInt());
			map.put(stack, buf.readInt());
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(EMCMapper.emc.size());
		
		for (Entry<SimpleStack, Integer> entry : EMCMapper.emc.entrySet())
		{
			SimpleStack key = entry.getKey();
			buf.writeInt(key.id);
			buf.writeInt(key.damage);
			buf.writeInt(key.qnty);
			buf.writeInt(entry.getValue());
		}
	}
}
