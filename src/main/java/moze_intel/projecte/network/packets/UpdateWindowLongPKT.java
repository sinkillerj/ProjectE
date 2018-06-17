package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.container.LongContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

// Version of SPacketWindowProperty that does not truncate the `value` arg to an int
public class UpdateWindowLongPKT implements IMessage
{

    private short windowId;
    private short propId;
    private long propVal;

    public UpdateWindowLongPKT() {}

    public UpdateWindowLongPKT(short windowId, short propId, long propVal)
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
        propVal = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(windowId);
        buf.writeShort(propId);
        buf.writeLong(propVal);
    }

    public static class Handler implements IMessageHandler<UpdateWindowLongPKT, IMessage>
    {
        @Override
        public IMessage onMessage(final UpdateWindowLongPKT msg, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                @Override
                public void run() {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    if (player.openContainer != null && player.openContainer.windowId == msg.windowId) {
                        //It should always be a LongContainer if it is this type of packet, if not fallback to normal update
                        if (player.openContainer instanceof LongContainer)
                        {
                            ((LongContainer) player.openContainer).updateProgressBarLong(msg.propId, msg.propVal);
                        }
                        else
                        {
                            player.openContainer.updateProgressBar(msg.propId, (int) msg.propVal);
                        }
                    }
                }
            });
            return null;
        }
    }
}
