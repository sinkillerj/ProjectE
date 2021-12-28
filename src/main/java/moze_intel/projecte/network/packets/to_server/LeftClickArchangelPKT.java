package moze_intel.projecte.network.packets.to_server;

import moze_intel.projecte.gameObjs.items.rings.ArchangelSmite;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public class LeftClickArchangelPKT implements IPEPacket {

	@Override
	public void handle(NetworkEvent.Context context) {
		Player player = context.getSender();
		if (player != null) {
			ItemStack main = player.getMainHandItem();
			if (!main.isEmpty() && main.getItem() instanceof ArchangelSmite archangelSmite) {
				archangelSmite.fireVolley(main, player);
			}
		}
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
	}

	public static LeftClickArchangelPKT decode(FriendlyByteBuf buffer) {
		return new LeftClickArchangelPKT();
	}
}