package moze_intel.projecte.network.packets;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.nbt.NBTTagCompound;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

public class SyncEmcPKT implements IMessage
{
	private int packetNum;
	private ArrayList<Entry<SimpleStack, Integer>> data;

	public SyncEmcPKT() {}

	public SyncEmcPKT(int packetNum, ArrayList<Entry<SimpleStack, Integer>> arrayList)
	{
		this.packetNum = packetNum;
		data = arrayList;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		packetNum = buf.readInt();
		int size = buf.readInt();
		data = Lists.newArrayList();
		for (int i = 0; i < size; i++)
		{
			int id = buf.readInt();
			int damage = buf.readInt();
			int qnty = buf.readInt();
			NBTTagCompound tag = ByteBufUtils.readTag(buf);
			int emc = buf.readInt();
			SimpleStack stack = new SimpleStack(id, qnty, damage, tag);
			data.add(new AbstractMap.SimpleEntry<SimpleStack, Integer>(stack, emc));
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(packetNum);
		buf.writeInt(data.size());

		for (Entry<SimpleStack, Integer> entry : data)
		{
			buf.writeInt(entry.getKey().id);
			buf.writeInt(entry.getKey().damage);
			buf.writeInt(entry.getKey().qnty);
			ByteBufUtils.writeTag(buf, entry.getKey().nbt);
			buf.writeInt(entry.getValue());
		}
	}

	public static class Handler implements IMessageHandler<SyncEmcPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SyncEmcPKT pkt, MessageContext ctx)
		{
			if (pkt.packetNum == 0)
			{
				PELogger.logInfo("Receiving EMC data from server.");

				EMCMapper.emc.clear();
				EMCMapper.emc = Maps.newLinkedHashMap();
			}

			for (Entry<SimpleStack, Integer> entry: pkt.data)
			{

				SimpleStack stack = entry.getKey();

				if (stack.isValid())
				{
					EMCMapper.emc.put(stack, entry.getValue());
				}
			}

			if (pkt.packetNum == -1)
			{
				PELogger.logInfo("Received all packets!");

				Transmutation.cacheFullKnowledge();
				FuelMapper.loadMap();
			}
			return null;
		}
	}
}
