package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class SwingItemPKT implements IMessage, IMessageHandler<SwingItemPKT, IMessage> {
	//Needs to have an empty constructor
	public SwingItemPKT() {
	}

	@Override
	public IMessage onMessage(SwingItemPKT message, MessageContext ctx) {
		Minecraft.getMinecraft().thePlayer.swingItem();
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}
}
