package moze_intel.projecte.network.packets.to_client;

import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.gameObjs.container.CondenserContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public record UpdateCondenserLockPKT(short windowId, @Nullable ItemInfo lockInfo) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && player.containerMenu instanceof CondenserContainer container && player.containerMenu.containerId == windowId) {
			container.updateLockInfo(lockInfo);
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeShort(windowId);
		if (lockInfo == null) {
			buffer.writeBoolean(false);
		} else {
			buffer.writeBoolean(true);
			buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, lockInfo.getItem());
			buffer.writeNbt(lockInfo.getNBT());
		}
	}

	public static UpdateCondenserLockPKT decode(FriendlyByteBuf buffer) {
		short windowId = buffer.readShort();
		ItemInfo lockInfo = null;
		if (buffer.readBoolean()) {
			lockInfo = ItemInfo.fromItem(buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS), buffer.readNbt());
		}
		return new UpdateCondenserLockPKT(windowId, lockInfo);
	}
}