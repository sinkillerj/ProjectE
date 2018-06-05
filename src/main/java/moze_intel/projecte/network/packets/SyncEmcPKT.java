package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.playerData.Transmutation;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class SyncEmcPKT implements IMessage
{
	private long[][] data;

	public SyncEmcPKT() {}

	public SyncEmcPKT(long[][] data)
	{
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int size = ByteBufUtils.readVarInt(buf, 5);
		data = new long[size][];

		for (int i = 0; i < size; i++)
		{
			long[] array = new long[3];

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
		ByteBufUtils.writeVarInt(buf, data.length, 5);

		for (long[] array : data)
		{
			for (int i = 0; i < 3; i++)
			{
				ByteBufUtils.writeVarInt(buf, (int) array[i], 5); //TODO BUGGY
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
					PECore.LOGGER.info("Receiving EMC data from server.");
					EMCMapper.emc.clear();

					for (long[] array : pkt.data)
					{
						Item i = Item.REGISTRY.getObjectById((int) array[0]);

						SimpleStack stack = new SimpleStack(i.getRegistryName(), (int) array[1]);

						if (stack.isValid())
						{
							EMCMapper.emc.put(stack, array[2]);
						}
					}

					Transmutation.cacheFullKnowledge();
					FuelMapper.loadMap();
				}
			});

			return null;
		}
	}
}
