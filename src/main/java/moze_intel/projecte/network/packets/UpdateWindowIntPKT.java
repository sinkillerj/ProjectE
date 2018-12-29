package moze_intel.projecte.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// Version of SPacketWindowProperty that does not truncate the `value` arg to a short
public class UpdateWindowIntPKT {
    private final short windowId;
    private final short propId;
    private final int propVal;

    public UpdateWindowIntPKT(short windowId, short propId, int propVal)
    {
        this.windowId = windowId;
        this.propId = propId;
        this.propVal = propVal;
    }

    public static void encode(UpdateWindowIntPKT msg, PacketBuffer buf)
    {
        buf.writeShort(msg.windowId);
        buf.writeShort(msg.propId);
        buf.writeVarInt(msg.propVal);
    }

    public static UpdateWindowIntPKT decode(PacketBuffer buf)
    {
        return new UpdateWindowIntPKT(buf.readShort(), buf.readShort(), buf.readVarInt());
    }

    public static class Handler
    {
        public static void handle(final UpdateWindowIntPKT msg, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
                EntityPlayer player = Minecraft.getInstance().player;
                if (player.openContainer != null && player.openContainer.windowId == msg.windowId)
                {
                    player.openContainer.updateProgressBar(msg.propId, msg.propVal);
                }
            });
        }
    }
}
