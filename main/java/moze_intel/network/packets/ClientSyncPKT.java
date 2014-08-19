package moze_intel.network.packets;

import io.netty.buffer.ByteBuf;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import moze_intel.MozeCore;
import moze_intel.EMC.EMCMapper;
import moze_intel.EMC.IStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ClientSyncPKT implements IMessage, IMessageHandler<ClientSyncPKT, IMessage>
{
	private static LinkedHashMap<IStack, Integer> map;
	
	public ClientSyncPKT() {}
	
	public ClientSyncPKT(LinkedHashMap map)
	{
		this.map = (LinkedHashMap) map.clone();
	}
	
	@Override
	public IMessage onMessage(ClientSyncPKT message, MessageContext ctx)
	{
		MozeCore.logger.logInfo("Receiving EMC mapping from the server.");
		
		EMCMapper.emc.clear();
		EMCMapper.emc = (LinkedHashMap<IStack, Integer>) message.map;
		
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		map = new LinkedHashMap();
		
		int size = buf.readInt();
		
		for (int i = 0; i < size; i++)
		{
			IStack stack = new IStack(buf.readInt(), buf.readInt(), buf.readInt());
			map.put(stack, buf.readInt());
		}
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(map.size());
		
		for (Entry<IStack, Integer> entry : map.entrySet())
		{
			IStack key = entry.getKey();
			buf.writeInt(key.id);
			buf.writeInt(key.damage);
			buf.writeInt(key.qnty);
			buf.writeInt(entry.getValue());
		}
	}
}
