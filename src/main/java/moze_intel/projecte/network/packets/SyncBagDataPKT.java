package moze_intel.projecte.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.playerData.AlchemicalBags;
import net.minecraft.nbt.NBTTagCompound;

public class SyncBagDataPKT implements IMessage, IMessageHandler<SyncBagDataPKT, IMessage>
{
	private NBTTagCompound nbt;
	
	public SyncBagDataPKT() {}
	
	public SyncBagDataPKT(NBTTagCompound nbt)
	{
		this.nbt = nbt;
	}

	@Override
	public IMessage onMessage(final SyncBagDataPKT message, MessageContext ctx)
	{
		Minecraft.getMinecraft().addScheduledTask(new Runnable() {
			@Override
			public void run() {
				AlchemicalBags.clear();
				AlchemicalBags.loadFromNBT(message.nbt);
			}
		});
		
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
