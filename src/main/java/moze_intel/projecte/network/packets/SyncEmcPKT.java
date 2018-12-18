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

public class SyncEmcPKT implements IMessage
{
	private EmcPKTInfo[] data;

	public SyncEmcPKT() {}

	public SyncEmcPKT(EmcPKTInfo[] data)
	{
		this.data = data;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int size = ByteBufUtils.readVarInt(buf, 5);
		data = new EmcPKTInfo[size];

		for (int i = 0; i < size; i++)
		{
			data[i] = new EmcPKTInfo(ByteBufUtils.readVarInt(buf, 5), ByteBufUtils.readVarInt(buf, 5), buf.readLong());
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		ByteBufUtils.writeVarInt(buf, data.length, 5);

		for (EmcPKTInfo info : data)
		{
			ByteBufUtils.writeVarInt(buf, info.getId(), 5);
			ByteBufUtils.writeVarInt(buf, info.getDamage(), 5);
			buf.writeLong(info.getEmc());
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

					for (EmcPKTInfo info : pkt.data)
					{
						Item i = Item.REGISTRY.getObjectById(info.getId());

						SimpleStack stack = new SimpleStack(i.getRegistryName(), info.getDamage());

						if (stack.isValid())
						{
							EMCMapper.emc.put(stack, info.getEmc());
						}
					}

					Transmutation.cacheFullKnowledge();
					FuelMapper.loadMap();
					PECore.refreshJEI();
				}
			});

			return null;
		}
	}

	public static class EmcPKTInfo {
		private int id, damage;
		private long emc;

		public EmcPKTInfo(int id, int damage, long emc) {
			this.id = id;
			this.damage = damage;
			this.emc = emc;
		}

		public int getDamage() {
			return damage;
		}

		public int getId() {
			return id;
		}

		public long getEmc() {
			return emc;
		}
	}
}
