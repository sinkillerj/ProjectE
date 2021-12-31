package moze_intel.projecte.network.packets.to_client.knowledge;

import java.util.HashMap;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record KnowledgeSyncInputsAndLocksPKT(Map<Integer, ItemStack> stacksToSync, TargetUpdateType updateTargets) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).ifPresent(cap -> {
				cap.receiveInputsAndLocks(stacksToSync);
				if (updateTargets != TargetUpdateType.NONE && player.containerMenu instanceof TransmutationContainer container) {
					//Update targets in case total available EMC is now different
					TransmutationInventory transmutationInventory = container.transmutationInventory;
					if (updateTargets == TargetUpdateType.ALL) {
						transmutationInventory.updateClientTargets();
					} else {//If needed
						transmutationInventory.checkForUpdates();
					}
				}
			});
		}
		PECore.debugLog("** RECEIVED TRANSMUTATION INPUT AND LOCK DATA CLIENTSIDE **");
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(stacksToSync.size());
		for (Map.Entry<Integer, ItemStack> entry : stacksToSync.entrySet()) {
			buffer.writeVarInt(entry.getKey());
			buffer.writeItem(entry.getValue());
		}
		buffer.writeEnum(updateTargets);
	}

	public static KnowledgeSyncInputsAndLocksPKT decode(FriendlyByteBuf buffer) {
		int size = buffer.readVarInt();
		Map<Integer, ItemStack> syncedStacks = new HashMap<>(size);
		for (int i = 0; i < size; i++) {
			syncedStacks.put(buffer.readVarInt(), buffer.readItem());
		}
		return new KnowledgeSyncInputsAndLocksPKT(syncedStacks, buffer.readEnum(TargetUpdateType.class));
	}
}