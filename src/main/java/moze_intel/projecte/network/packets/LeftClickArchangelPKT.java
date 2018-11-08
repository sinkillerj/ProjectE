package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.rings.ArchangelSmite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class LeftClickArchangelPKT implements IMessage
{
	@Override
	public void fromBytes(ByteBuf buf) {}

	@Override
	public void toBytes(ByteBuf buf) {}

	public static class Handler implements IMessageHandler<LeftClickArchangelPKT, IMessage>
	{
		@Override
		public IMessage onMessage(LeftClickArchangelPKT message, MessageContext ctx)
		{
			ctx.getServerHandler().player.server.addScheduledTask(new Runnable() {
				@Override
				public void run() {
					EntityPlayer player = ctx.getServerHandler().player;
					ItemStack main = player.getHeldItemMainhand();
					if (!main.isEmpty() && main.getItem() == ObjHandler.angelSmite)
					{
						((ArchangelSmite) ObjHandler.angelSmite).fireVolley(main, player);
					}
				}
			});

			return null;
		}
	}
}
