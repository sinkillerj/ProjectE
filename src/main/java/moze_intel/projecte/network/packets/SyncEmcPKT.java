package moze_intel.projecte.network.packets;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.PELogger;

import java.util.List;
import java.util.Map;

public class SyncEmcPKT implements IMessage
{
	private int packetNum;
	private Object[] data;
	private boolean isCreateEmc;

	public SyncEmcPKT() {}

	public SyncEmcPKT(int packetNum, boolean isCreateEmc, List<Integer[]> arrayList)
	{
		this.packetNum = packetNum;
		this.isCreateEmc = isCreateEmc;
		data = arrayList.toArray();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		packetNum = buf.readInt();
		int size = buf.readInt();
		data = new Object[size];

		for (int i = 0; i < size; i++)
		{
			Integer[] array = new Integer[4];

			for (int j = 0; j < 4; j++)
			{
				array[j] = buf.readInt();
			}

			data[i] = array;
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(packetNum);
		buf.writeInt(data.length);

		for (Object obj : data)
		{
			Integer[] array = (Integer[]) obj;

			for (int i = 0; i < 4; i++)
			{
				buf.writeInt(array[i]);
			}
		}
	}

	public static class Handler implements IMessageHandler<SyncEmcPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SyncEmcPKT pkt, MessageContext ctx)
		{
			if (pkt.packetNum == 0)
			{
				if (pkt.isCreateEmc) {
					PELogger.logInfo("Receiving create-EMC data from server.");
					EMCMapper.emcForCreation = Maps.newLinkedHashMap();;
				} else {
					PELogger.logInfo("Receiving destroy-EMC data from server.");
					EMCMapper.emcForDestruction = Maps.newLinkedHashMap();;
				}
			}

			for (Object obj : pkt.data)
			{
				Integer[] array = (Integer[]) obj;

				SimpleStack stack = new SimpleStack(array[0], array[1], array[2]);

				if (stack.isValid())
				{
					Map<SimpleStack, Integer> m;
					if (pkt.isCreateEmc) {
						m = EMCMapper.emcForCreation;
					} else {
						m = EMCMapper.emcForDestruction;
					}
					m.put(stack, array[3]);
				}
			}

			if (pkt.packetNum == -1)
			{
				PELogger.logInfo("Received all packets!");
				if (pkt.isCreateEmc) {
					EMCMapper.emcForCreation = ImmutableMap.copyOf(EMCMapper.emcForCreation);
				} else {
					EMCMapper.emcForDestruction = ImmutableMap.copyOf(EMCMapper.emcForDestruction);
				}

				Transmutation.cacheFullKnowledge();
				FuelMapper.loadMap();
			}
			return null;
		}
	}
}
