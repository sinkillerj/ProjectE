package moze_intel.projecte.network.packets;

import moze_intel.projecte.PECore;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class KnowledgeClearPKT implements IMessage
{
	public static void encode(KnowledgeClearPKT msg, PacketBuffer buf) {}

	public static KnowledgeClearPKT decode(PacketBuffer buf)
	{
		return new KnowledgeClearPKT();
	}

	public static class Handler
	{
		public static void handle(KnowledgeClearPKT pkt, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> PECore.proxy.clearClientKnowledge());
		}
	}
}
