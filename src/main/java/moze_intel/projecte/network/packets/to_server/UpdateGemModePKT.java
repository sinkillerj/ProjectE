package moze_intel.projecte.network.packets.to_server;

import moze_intel.projecte.gameObjs.items.GemEternalDensity;
import moze_intel.projecte.network.packets.IPEPacket;
import moze_intel.projecte.utils.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class UpdateGemModePKT implements IPEPacket {

	private final boolean mode;

	public UpdateGemModePKT(boolean mode) {
		this.mode = mode;
	}

	@Override
	public void handle(Context context) {
		PlayerEntity player = context.getSender();
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
	public void encode(PacketBuffer buffer) {
		buffer.writeBoolean(mode);
	}

	public static UpdateGemModePKT decode(PacketBuffer buffer) {
		return new UpdateGemModePKT(buffer.readBoolean());
	}
}