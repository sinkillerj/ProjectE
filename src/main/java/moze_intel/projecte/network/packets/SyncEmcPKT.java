package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class SyncEmcPKT implements IMessage
{
	private int packetNum;
	private Object[] data;

	public SyncEmcPKT() {}

	public SyncEmcPKT(int packetNum, List<Integer[]> arrayList)
	{
		this.packetNum = packetNum;
		data = arrayList.toArray();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		packetNum = ByteBufUtils.readVarInt(buf, 5);
		int size = buf.readShort();
		data = new Object[size];

		for (int i = 0; i < size; i++)
		{
			Integer[] array = new Integer[4];

			for (int j = 0; j < 3; j++)
			{
				array[j] = ByteBufUtils.readVarInt(buf, 5);
			}

			data[i] = array;
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, packetNum, 5);
		buf.writeShort(data.length);

		for (Object obj : data)
		{
			Integer[] array = (Integer[]) obj;

			for (int i = 0; i < 3; i++)
			{
				ByteBufUtils.writeVarInt(buf, array[i], 5);
			}
		}
	}

	public static class Handler implements IMessageHandler<SyncEmcPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SyncEmcPKT pkt, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					if (pkt.packetNum == 0)
					{
						PELogger.logInfo("Receiving EMC data from server.");

						EMCMapper.emc.clear();
					}

					for (Object obj : pkt.data)
					{
						Integer[] array = (Integer[]) obj;

						Item i = Item.REGISTRY.getObjectById(array[0]);

						SimpleStack stack = new SimpleStack(i.getRegistryName(), array[1]);

						if (stack.isValid())
						{
							EMCMapper.emc.put(stack, array[2]);
						}
					}

					if (pkt.packetNum == -1)
					{
						PELogger.logInfo("Received all packets!");

						Transmutation.cacheFullKnowledge();
						FuelMapper.loadMap();
					}
				}
			});

			return null;
		}
	}
}
