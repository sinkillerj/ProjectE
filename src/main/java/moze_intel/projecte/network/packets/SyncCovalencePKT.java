package moze_intel.projecte.network.packets;

import moze_intel.projecte.emc.EMCMapper;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCovalencePKT {
	private final double covalenceLoss;

	public SyncCovalencePKT(double value)
	{
		covalenceLoss = value;
	}

	public static void encode(SyncCovalencePKT msg, PacketBuffer buf)
	{
		buf.writeDouble(msg.covalenceLoss);
	}

	public static SyncCovalencePKT decode(PacketBuffer buf)
	{
		return new SyncCovalencePKT(buf.readDouble());
	}

	public static class Handler
	{
		public static void handle(final SyncCovalencePKT message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> EMCMapper.covalenceLoss = message.covalenceLoss);
			ctx.get().setPacketHandled(true);
		}
	}
}
