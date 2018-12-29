package moze_intel.projecte.network.packets;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateGemModePKT {
	private final boolean mode;

	public UpdateGemModePKT(boolean mode)
	{
		this.mode = mode;
	}

	public static void encode(UpdateGemModePKT msg, PacketBuffer buf)
	{
		buf.writeBoolean(msg.mode);
	}

	public static UpdateGemModePKT decode(PacketBuffer buf)
	{
		return new UpdateGemModePKT(buf.readBoolean());
	}

	public static class Handler
	{
		public static void handle(final UpdateGemModePKT pkt, final Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> {
				ItemStack stack = ctx.get().getSender().getHeldItem(EnumHand.MAIN_HAND);
				if (stack.isEmpty())
					stack = ctx.get().getSender().getHeldItem(EnumHand.OFF_HAND);

				if (!stack.isEmpty() && (stack.getItem() == ObjHandler.eternalDensity || stack.getItem() == ObjHandler.voidRing))
				{
					stack.getTag().putBoolean("Whitelist", pkt.mode);
				}
			});
		}
	}
}
