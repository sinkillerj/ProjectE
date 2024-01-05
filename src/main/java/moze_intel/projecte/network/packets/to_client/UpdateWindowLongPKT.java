package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.container.PEContainer;
import moze_intel.projecte.network.PacketUtils;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import org.jetbrains.annotations.NotNull;

// Version of SWindowPropertyPacket that supports long values
public record UpdateWindowLongPKT(short windowId, short propId, long propVal) implements IPEPacket<PlayPayloadContext> {

	public static final ResourceLocation ID = PECore.rl("update_window_long");

	public UpdateWindowLongPKT(FriendlyByteBuf buffer) {
		this(buffer.readShort(), buffer.readShort(), buffer.readLong());
	}

	@NotNull
	@Override
	public ResourceLocation id() {
		return ID;
	}

	@Override
	public void handle(PlayPayloadContext context) {
		PacketUtils.container(context, PEContainer.class)
				.filter(container -> container.containerId == windowId)
				.ifPresent(container -> container.updateProgressBarLong(propId, propVal));
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buffer) {
		buffer.writeShort(windowId);
		buffer.writeShort(propId);
		buffer.writeLong(propVal);
	}
}