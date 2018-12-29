package moze_intel.projecte.network.packets;

import moze_intel.projecte.PECore;
import moze_intel.projecte.network.ThreadCheckUpdate;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CheckUpdatePKT {
	public static void encode(CheckUpdatePKT pkt, PacketBuffer buf) {}

	public static CheckUpdatePKT decode(PacketBuffer buf) {
		return new CheckUpdatePKT();
	}

	public static class Handler
	{
		public static void handle(CheckUpdatePKT msg, Supplier<NetworkEvent.Context> ctx)
		{
			if (!ThreadCheckUpdate.hasRun() && updateCheckEnabled())
			{
				new ThreadCheckUpdate().start();
			}
		}

		private static boolean updateCheckEnabled()
		{
			return ForgeModContainer.getConfig().get(ForgeModContainer.VERSION_CHECK_CAT, "Global", true).getBoolean()
					&& ForgeModContainer.getConfig().get(ForgeModContainer.VERSION_CHECK_CAT, PECore.MODID, true).getBoolean();
		}
	}
}
