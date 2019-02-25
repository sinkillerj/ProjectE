package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.gui.GUIAlchChest;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.items.ItemStackHandler;

public class ShowBagPKT implements IMessage
{
	private int windowId;

	public ShowBagPKT() {}

	public ShowBagPKT(int windowId)
	{
		this.windowId = windowId;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		windowId = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeByte(windowId);
	}

	public static class Handler implements IMessageHandler<ShowBagPKT, IMessage>
	{
		@Override
		public IMessage onMessage(ShowBagPKT message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(new Runnable() {
				@Override
				public void run() {
					Minecraft.getMinecraft().displayGuiScreen(new GUIAlchChest(Minecraft.getMinecraft().player.inventory, EnumHand.OFF_HAND, new ItemStackHandler(104)));
					Minecraft.getMinecraft().player.openContainer.windowId = message.windowId;
				}
			});
			return null;
		}
	}
}
