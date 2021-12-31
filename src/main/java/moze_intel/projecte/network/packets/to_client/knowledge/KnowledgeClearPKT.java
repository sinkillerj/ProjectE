package moze_intel.projecte.network.packets.to_client.knowledge;

import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class KnowledgeClearPKT implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		if (Minecraft.getInstance().player != null) {
			Minecraft.getInstance().player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent(IKnowledgeProvider::clearKnowledge);
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
	}

	public static KnowledgeClearPKT decode(FriendlyByteBuf buffer) {
		return new KnowledgeClearPKT();
	}
}