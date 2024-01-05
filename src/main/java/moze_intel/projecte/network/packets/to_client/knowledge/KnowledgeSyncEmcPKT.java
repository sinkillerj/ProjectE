package moze_intel.projecte.network.packets.to_client.knowledge;

import java.math.BigInteger;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record KnowledgeSyncEmcPKT(BigInteger emc) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("knowledge_sync_emc");

	public KnowledgeSyncEmcPKT(FriendlyByteBuf buffer) {
		this(buffer.readUtf());
	}

	private KnowledgeSyncEmcPKT(String emc) {
		this(emc.isEmpty() ? BigInteger.ZERO : new BigInteger(emc));
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(PlayPayloadContext context) {
		context.player().ifPresent(player -> {
			IKnowledgeProvider knowledge = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY);
			if (knowledge != null) {
				knowledge.setEmc(emc);
				if (player.containerMenu instanceof TransmutationContainer container) {
					container.transmutationInventory.updateClientTargets();
				}
			}
		});
		PECore.debugLog("** RECEIVED TRANSMUTATION EMC DATA CLIENTSIDE **");
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeUtf(emc.toString());
	}
}