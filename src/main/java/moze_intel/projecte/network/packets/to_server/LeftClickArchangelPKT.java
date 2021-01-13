package moze_intel.projecte.network.packets.to_server;

import moze_intel.projecte.gameObjs.items.rings.ArchangelSmite;
import moze_intel.projecte.network.packets.IPEPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class LeftClickArchangelPKT implements IPEPacket {

	@Override
	public void handle(Context context) {
		PlayerEntity player = context.getSender();
		if (player != null) {
			ItemStack main = player.getHeldItemMainhand();
			if (!main.isEmpty() && main.getItem() instanceof ArchangelSmite) {
				((ArchangelSmite) main.getItem()).fireVolley(main, player);
			}
		}
	}

	@Override
	public void encode(PacketBuffer buffer) {
	}

	public static LeftClickArchangelPKT decode(PacketBuffer buffer) {
		return new LeftClickArchangelPKT();
	}
}