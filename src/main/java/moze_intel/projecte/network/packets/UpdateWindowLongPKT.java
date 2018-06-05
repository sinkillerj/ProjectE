package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

// Version of SPacketWindowProperty that does not truncate the `value` arg to a short
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
                        long val = msg.propVal;
                        int counter = 0;
                        if (val != 0) {
                            while (val > 0) {
                                int num = Integer.MAX_VALUE;
                                if (val < Integer.MAX_VALUE) {
                                    num = (int) val;
                                }
                                player.openContainer.updateProgressBar(10 * counter + msg.propId, num);
                                val -= num;
                                counter++;
                            }
                        } else {
                            player.openContainer.updateProgressBar(msg.propId, 0);
                        }
                    }
                }
            });
            return null;
        }
    }
}
