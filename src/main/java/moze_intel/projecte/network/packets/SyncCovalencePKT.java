package moze_intel.projecte.network.packets;

import moze_intel.projecte.emc.EMCMappingHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncCovalencePKT {
	private final double covalenceLoss;
	private boolean covalenceLossRounding;

	public SyncCovalencePKT(double value, boolean rounding)
	{
		covalenceLoss = value;
		covalenceLossRounding = rounding;
	}

	public static void encode(SyncCovalencePKT msg, PacketBuffer buf)
	{
		buf.writeDouble(msg.covalenceLoss);
		buf.writeBoolean(msg.covalenceLossRounding);
	}

	public static SyncCovalencePKT decode(PacketBuffer buf)
	{
		return new SyncCovalencePKT(buf.readDouble(), buf.readBoolean());
	}

	public static class Handler
	{
		public static void handle(final SyncCovalencePKT message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> {
				EMCMappingHandler.covalenceLoss = message.covalenceLoss;
				EMCMappingHandler.covalenceLossRounding = message.covalenceLossRounding;
			});
			ctx.get().setPacketHandled(true);
		}
	}
}
