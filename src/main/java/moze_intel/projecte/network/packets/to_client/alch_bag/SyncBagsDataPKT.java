package moze_intel.projecte.network.packets.to_client.alch_bag;

import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.impl.capability.AlchBagImpl.AlchemicalBagAttachment;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.DyeColor;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SyncBagsDataPKT(Map<DyeColor, PEItemStacksHandler> handlers) implements IPEPacket {

	public static final CustomPacketPayload.Type<SyncBagsDataPKT> TYPE = new CustomPacketPayload.Type<>(PECore.rl("sync_bag_data"));
	public static final StreamCodec<RegistryFriendlyByteBuf, SyncBagsDataPKT> STREAM_CODEC = AlchemicalBagAttachment.MAP_STREAM_CODEC.map(
			SyncBagsDataPKT::new, SyncBagsDataPKT::handlers
	);

	@NotNull
	@Override
	public CustomPacketPayload.Type<SyncBagsDataPKT> type() {
		return TYPE;
	}

	@Override
	public void handle(IPayloadContext context) {
		//We have to use the client's player instance rather than context#player as the first usage of this packet is sent during player login
		// which is before the player exists on the client so the context does not contain it.
		//Note: This must stay LocalPlayer to not cause classloading issues
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			player.getData(PEAttachmentTypes.ALCHEMICAL_BAGS).updateBags(handlers);
		}
		PECore.debugLog("** RECEIVED BAGS CLIENTSIDE **");
	}
}
