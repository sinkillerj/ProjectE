package moze_intel.projecte.network.packets.to_client.knowledge;

import java.math.BigInteger;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public record KnowledgeSyncEmcPKT(BigInteger emc) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(cap -> {
				cap.setEmc(emc);
				if (player.containerMenu instanceof TransmutationContainer container) {
					container.transmutationInventory.updateClientTargets();
				}
			});
		}
		PECore.debugLog("** RECEIVED TRANSMUTATION EMC DATA CLIENTSIDE **");
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeUtf(emc.toString());
	}

	public static KnowledgeSyncEmcPKT decode(FriendlyByteBuf buffer) {
		String emc = buffer.readUtf();
		return new KnowledgeSyncEmcPKT(emc.isEmpty() ? BigInteger.ZERO : new BigInteger(emc));
	}
}