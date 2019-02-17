package moze_intel.projecte.network.packets;

import moze_intel.projecte.PECore;
import moze_intel.projecte.network.ThreadCheckUpdate;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.loading.FMLConfig;
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
			ctx.get().setPacketHandled(true);
		}

		private static boolean updateCheckEnabled()
		{
			return FMLConfig.runVersionCheck();
		}
	}
}
