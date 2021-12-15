package moze_intel.projecte.network.packets.to_client;

import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class UpdateCondenserLockPKT implements IPEPacket {

	@Nullable
	private final ItemInfo lockInfo;
	private final short windowId;

	public UpdateCondenserLockPKT(short windowId, @Nullable ItemInfo lockInfo) {
		this.windowId = windowId;
		this.lockInfo = lockInfo;
	}

	@Override
	public void handle(Context context) {
		ClientPlayerEntity player = Minecraft.getInstance().player;
		if (player != null && player.containerMenu instanceof CondenserContainer && player.containerMenu.containerId == windowId) {
			((CondenserContainer) player.containerMenu).updateLockInfo(lockInfo);
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeShort(windowId);
		if (lockInfo == null) {
			buffer.writeBoolean(false);
		} else {
			buffer.writeBoolean(true);
			buffer.writeRegistryId(lockInfo.getItem());
			buffer.writeNbt(lockInfo.getNBT());
		}
	}

	public static UpdateCondenserLockPKT decode(PacketBuffer buffer) {
		short windowId = buffer.readShort();
		ItemInfo lockInfo = null;
		if (buffer.readBoolean()) {
			lockInfo = ItemInfo.fromItem(buffer.readRegistryId(), buffer.readNbt());
		}
		return new UpdateCondenserLockPKT(windowId, lockInfo);
	}
}