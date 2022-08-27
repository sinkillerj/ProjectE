package moze_intel.projecte.network.packets.to_server;

import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record SearchUpdatePKT(int slot, ItemStack itemStack) implements IPEPacket {

	public SearchUpdatePKT {
		itemStack = itemStack.copy();
	}

	@Override
	public void handle(NetworkEvent.Context context) {
		Player player = context.getSender();
		if (player != null && player.containerMenu instanceof TransmutationContainer container) {
			container.transmutationInventory.writeIntoOutputSlot(slot, itemStack);
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeVarInt(slot);
		buffer.writeItem(itemStack);
	}

	public static SearchUpdatePKT decode(FriendlyByteBuf buffer) {
		return new SearchUpdatePKT(buffer.readVarInt(), buffer.readItem());
	}
}