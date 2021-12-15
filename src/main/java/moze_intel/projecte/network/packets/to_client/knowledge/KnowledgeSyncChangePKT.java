package moze_intel.projecte.network.packets.to_client.knowledge;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class KnowledgeSyncChangePKT implements IPEPacket {

	private final ItemInfo change;
	private final boolean learned;

	public KnowledgeSyncChangePKT(ItemInfo change, boolean learned) {
		this.change = change;
		this.learned = learned;
	}

	@Override
	public void handle(Context context) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null) {
			player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(cap -> {
				if (learned) {
					if (!cap.hasKnowledge(change) && cap.addKnowledge(change) && player.containerMenu instanceof TransmutationContainer) {
						((TransmutationContainer) player.containerMenu).transmutationInventory.itemLearned();
					}
				} else if (cap.hasKnowledge(change) && cap.removeKnowledge(change) && player.containerMenu instanceof TransmutationContainer) {
					((TransmutationContainer) player.containerMenu).transmutationInventory.itemUnlearned();
				}
			});
		}
		PECore.debugLog("** RECEIVED TRANSMUTATION KNOWLEDGE CHANGE DATA CLIENTSIDE **");
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeRegistryId(change.getItem());
		buffer.writeNbt(change.getNBT());
		buffer.writeBoolean(learned);
	}

	public static KnowledgeSyncChangePKT decode(PacketBuffer buffer) {
		return new KnowledgeSyncChangePKT(ItemInfo.fromItem(buffer.readRegistryId(), buffer.readNbt()), buffer.readBoolean());
	}
}