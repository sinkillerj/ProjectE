package moze_intel.projecte.network.packets;

import moze_intel.projecte.PECore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBagDataPKT {
	private final NBTTagCompound nbt;

	public SyncBagDataPKT(NBTTagCompound nbt)
	{
		this.nbt = nbt;
	}

	public static void encode(SyncBagDataPKT msg, PacketBuffer buf)
	{
		buf.writeCompoundTag(msg.nbt);
	}

	public static SyncBagDataPKT decode(PacketBuffer buf)
	{
		return new SyncBagDataPKT(buf.readCompoundTag());
	}

	public static class Handler
	{
		public static void handle(final SyncBagDataPKT message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork(() -> {
				PECore.proxy.getClientBagProps().deserializeNBT(message.nbt);
				PECore.debugLog("** RECEIVED BAGS CLIENTSIDE **");
			});
			ctx.get().setPacketHandled(true);
		}
	}
}
