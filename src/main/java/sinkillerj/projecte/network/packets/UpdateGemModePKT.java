package sinkillerj.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import sinkillerj.projecte.gameObjs.ObjHandler;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class UpdateGemModePKT implements IMessage, IMessageHandler<UpdateGemModePKT, IMessage>
{
	private boolean mode;
	
	public UpdateGemModePKT() {}
	
	public UpdateGemModePKT(boolean mode) 
	{
		
		this.mode = mode;
	}
	
	@Override
	public IMessage onMessage(UpdateGemModePKT pkt, MessageContext ctx) 
	{
		ItemStack stack = ctx.getServerHandler().playerEntity.getHeldItem();
		
		if (stack != null && stack.getItem() == ObjHandler.eternalDensity)
		{
			stack.stackTagCompound.setBoolean("Whitelist", pkt.mode);
		}
		
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
