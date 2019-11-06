package moze_intel.projecte.network.packets;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateCondenserLockPKT {

	@Nullable
	private final ItemInfo lockInfo;
	private final short windowId;

	public UpdateCondenserLockPKT(short windowId, @Nullable ItemInfo lockInfo) {
		this.windowId = windowId;
		this.lockInfo = lockInfo;
	}

	public static void encode(UpdateCondenserLockPKT msg, PacketBuffer buf) {
		buf.writeShort(msg.windowId);
		if (msg.lockInfo == null) {
			buf.writeBoolean(false);
		} else {
			buf.writeBoolean(true);
			buf.writeRegistryId(msg.lockInfo.getItem());
			buf.writeCompoundTag(msg.lockInfo.getNBT());
		}
	}

	public static UpdateCondenserLockPKT decode(PacketBuffer buf) {
		short windowId = buf.readShort();
		boolean hasInfo = buf.readBoolean();
		ItemInfo lockInfo = null;
		if (hasInfo) {
			lockInfo = ItemInfo.fromItem(buf.readRegistryId(), buf.readCompoundTag());
		}
		return new UpdateCondenserLockPKT(windowId, lockInfo);
	}

	public static class Handler {

		public static void handle(final UpdateCondenserLockPKT msg, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> handleClient(msg));
			ctx.get().setPacketHandled(true);
		}

		//Needed to make the server be able to resolve registering the packet
		@OnlyIn(Dist.CLIENT)
		private static void handleClient(final UpdateCondenserLockPKT msg) {
			PlayerEntity player = Minecraft.getInstance().player;
			if (player.openContainer instanceof CondenserContainer && player.openContainer.windowId == msg.windowId) {
				((CondenserContainer) player.openContainer).updateLockInfo(msg.lockInfo);
			}
		}
	}
}