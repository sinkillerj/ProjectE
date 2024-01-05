package moze_intel.projecte.network.packets.to_client.knowledge;

import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

public record KnowledgeSyncInputsAndLocksPKT(Map<Integer, ItemStack> stacksToSync, TargetUpdateType updateTargets) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("knowledge_sync_inputs_and_locks");

	public KnowledgeSyncInputsAndLocksPKT(FriendlyByteBuf buffer) {
		this(buffer.readMap(FriendlyByteBuf::readVarInt, FriendlyByteBuf::readItem), buffer.readEnum(TargetUpdateType.class));
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
				knowledge.receiveInputsAndLocks(stacksToSync);
				if (updateTargets != TargetUpdateType.NONE && player.containerMenu instanceof TransmutationContainer container) {
					//Update targets in case total available EMC is now different
					TransmutationInventory transmutationInventory = container.transmutationInventory;
					if (updateTargets == TargetUpdateType.ALL) {
						transmutationInventory.updateClientTargets();
					} else {//If needed
						transmutationInventory.checkForUpdates();
					}
				}
			}
		});
		PECore.debugLog("** RECEIVED TRANSMUTATION INPUT AND LOCK DATA CLIENTSIDE **");
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeMap(stacksToSync, FriendlyByteBuf::writeVarInt, FriendlyByteBuf::writeItem);
		buffer.writeEnum(updateTargets);
	}
}