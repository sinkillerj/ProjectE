package moze_intel.projecte.network.packets;

import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SearchUpdatePKT {
	public final int slot;
	public final ItemStack itemStack;
	public SearchUpdatePKT(int slot, ItemStack itemStack)
	{
		this.slot = slot;
		this.itemStack = itemStack.copy();
	}

	public static void encode(SearchUpdatePKT msg, PacketBuffer buf)
	{
		buf.writeVarInt(msg.slot);
		buf.writeItemStack(msg.itemStack);
	}

	public static SearchUpdatePKT decode(PacketBuffer buf)
	{
		return new SearchUpdatePKT(buf.readVarInt(), buf.readItemStack());
	}

	public static class Handler
	{
		public static void handle(final SearchUpdatePKT pkt, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> {
				if (ctx.get().getSender().openContainer instanceof TransmutationContainer)
				{
					TransmutationContainer container = ((TransmutationContainer) ctx.get().getSender().openContainer);
					container.transmutationInventory.writeIntoOutputSlot(pkt.slot, pkt.itemStack);
				}
			});
			ctx.get().setPacketHandled(true);
		}
	}
}
