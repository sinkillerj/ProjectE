package moze_intel.projecte.network.packets.to_server;

import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.network.packets.IPEPacket;
import moze_intel.projecte.utils.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record UpdateGemModePKT(boolean mode) implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		Player player = context.getSender();
		if (player != null) {
			ItemStack stack = player.getMainHandItem();
			if (stack.isEmpty()) {
				stack = player.getOffhandItem();
			}
			if (!stack.isEmpty() && stack.getItem() instanceof GemEternalDensity) {
				//Note: Void Ring extends gem of eternal density so we only need to check if it is an instance of the base class
				stack.getOrCreateTag().putBoolean(Constants.NBT_KEY_GEM_WHITELIST, mode);
			}
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeBoolean(mode);
	}

	public static UpdateGemModePKT decode(FriendlyByteBuf buffer) {
		return new UpdateGemModePKT(buffer.readBoolean());
	}
}