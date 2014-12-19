package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class SetFlyPKT implements IMessage, IMessageHandler<SetFlyPKT, IMessage>
{
	private boolean flag;
		
	//Needs to have an empty constructor
	public SetFlyPKT() {}
		
	public SetFlyPKT(boolean value)
	{
		flag = value;
	}
		
	@Override
	public IMessage onMessage(SetFlyPKT message, MessageContext ctx) 
	{
		Minecraft.getMinecraft().thePlayer.capabilities.allowFlying = message.flag;
		
		if (!flag)
		{
			Minecraft.getMinecraft().thePlayer.capabilities.isFlying = false;
		}
		
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		flag = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeBoolean(flag);
	}
}
