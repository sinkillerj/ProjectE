package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

// Version of SPacketWindowProperty that does not truncate the `value` arg to a short
public class UpdateWindowIntPKT implements IMessage
{

    private short windowId;
    private short propId;
    private int propVal;

    public UpdateWindowIntPKT() {}

    public UpdateWindowIntPKT(short windowId, short propId, int propVal)
    {
        this.windowId = windowId;
        this.propId = propId;
        this.propVal = propVal;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        windowId = buf.readUnsignedByte();
        propId = buf.readShort();
        propVal = ByteBufUtils.readVarInt(buf, 5);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(windowId);
        buf.writeShort(propId);
        ByteBufUtils.writeVarInt(buf, propVal, 5);
    }

    public static class Handler implements IMessageHandler<UpdateWindowIntPKT, IMessage>
    {
        @Override
        public IMessage onMessage(final UpdateWindowIntPKT msg, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    if (player.openContainer != null && player.openContainer.windowId == msg.windowId)
                    {
                        player.openContainer.updateProgressBar(msg.propId, msg.propVal);
                    }
                }
            });
            return null;
        }
    }
}
