package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateGemModePKT implements IMessage, IMessageHandler<UpdateGemModePKT, IMessage>
{
	private boolean mode;
	
	public UpdateGemModePKT() {}
	
	public UpdateGemModePKT(boolean mode) 
	{
		
		this.mode = mode;
	}
	
	@Override
	public IMessage onMessage(final UpdateGemModePKT pkt, final MessageContext ctx)
	{
		ctx.getServerHandler().playerEntity.mcServer.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				ItemStack stack = ctx.getServerHandler().playerEntity.getHeldItem();

				if (stack != null && stack.getItem() == ObjHandler.eternalDensity)
				{
					stack.getTagCompound().setBoolean("Whitelist", pkt.mode);
				}
			}
		});

		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		mode = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeBoolean(mode);
	}
}
