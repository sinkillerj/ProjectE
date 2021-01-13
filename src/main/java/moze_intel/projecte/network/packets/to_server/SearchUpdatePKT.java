package moze_intel.projecte.network.packets.to_server;

import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SearchUpdatePKT implements IPEPacket {

	public final int slot;
	public final ItemStack itemStack;

	public SearchUpdatePKT(int slot, ItemStack itemStack) {
		this.slot = slot;
		this.itemStack = itemStack.copy();
	}

	@Override
	public void handle(Context context) {
		PlayerEntity player = context.getSender();
		if (player != null && player.openContainer instanceof TransmutationContainer) {
			((TransmutationContainer) player.openContainer).transmutationInventory.writeIntoOutputSlot(slot, itemStack);
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeVarInt(slot);
		buffer.writeItemStack(itemStack);
	}

	public static SearchUpdatePKT decode(PacketBuffer buffer) {
		return new SearchUpdatePKT(buffer.readVarInt(), buffer.readItemStack());
	}
}