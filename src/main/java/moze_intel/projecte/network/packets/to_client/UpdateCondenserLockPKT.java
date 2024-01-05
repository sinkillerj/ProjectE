package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.network.PacketUtils;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record UpdateCondenserLockPKT(short windowId, @Nullable ItemInfo lockInfo) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("update_condenser_lock");

	public UpdateCondenserLockPKT(FriendlyByteBuf buffer) {
		this(buffer.readShort(), buffer.readNullable(ItemInfo::read));
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(PlayPayloadContext context) {
		PacketUtils.container(context, CondenserContainer.class)
				.filter(container -> container.containerId == windowId)
				.ifPresent(container -> container.updateLockInfo(lockInfo));
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeShort(windowId);
		buffer.writeNullable(lockInfo, (buf, info) -> info.write(buf));
	}
}