package moze_intel.projecte.network.packets;

import moze_intel.projecte.gameObjs.gui.GUIAlchChest;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Supplier;

public class ShowBagPKT {
	private final int windowId;

	public ShowBagPKT(int windowId)
	{
		this.windowId = windowId;
	}

	public static void encode(ShowBagPKT msg, PacketBuffer buf)
	{
		buf.writeVarInt(msg.windowId);
	}

	public static ShowBagPKT decode(PacketBuffer buf)
	{
		return new ShowBagPKT(buf.readVarInt());
	}

	public static class Handler
	{
		public static void handle(ShowBagPKT message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> {
				Minecraft.getInstance().displayGuiScreen(new GUIAlchChest(Minecraft.getInstance().player.inventory, EnumHand.OFF_HAND, new ItemStackHandler(104)));
				Minecraft.getInstance().player.openContainer.windowId = message.windowId;
			});
		}
	}
}
