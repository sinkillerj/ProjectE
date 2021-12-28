package moze_intel.projecte.network.packets.to_client.knowledge;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class KnowledgeSyncChangePKT implements IPEPacket {

	private final ItemInfo change;
	private final boolean learned;

	public KnowledgeSyncChangePKT(ItemInfo change, boolean learned) {
		this.change = change;
		this.learned = learned;
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(cap -> {
				if (learned) {
					if (!cap.hasKnowledge(change) && cap.addKnowledge(change) && player.containerMenu instanceof TransmutationContainer container) {
						container.transmutationInventory.itemLearned();
					}
				} else if (cap.hasKnowledge(change) && cap.removeKnowledge(change) && player.containerMenu instanceof TransmutationContainer container) {
					container.transmutationInventory.itemUnlearned();
				}
			});
		}
		PECore.debugLog("** RECEIVED TRANSMUTATION KNOWLEDGE CHANGE DATA CLIENTSIDE **");
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeRegistryId(change.getItem());
		buffer.writeNbt(change.getNBT());
		buffer.writeBoolean(learned);
	}

	public static KnowledgeSyncChangePKT decode(FriendlyByteBuf buffer) {
		return new KnowledgeSyncChangePKT(ItemInfo.fromItem(buffer.readRegistryId(), buffer.readNbt()), buffer.readBoolean());
	}
}