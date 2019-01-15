package moze_intel.projecte.network.packets;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class KnowledgeClearPKT {
	public static void encode(KnowledgeClearPKT msg, PacketBuffer buf) {}

	public static KnowledgeClearPKT decode(PacketBuffer buf)
	{
		return new KnowledgeClearPKT();
	}

	public static class Handler
	{
		public static void handle(KnowledgeClearPKT pkt, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> Minecraft.getInstance().player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(IKnowledgeProvider::clearKnowledge));
			ctx.get().setPacketHandled(true);
		}
	}
}
