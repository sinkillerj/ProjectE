package moze_intel.projecte.network.packets;

import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncPedestalPKT implements IMessage
{
	public BlockPos pos;
	public boolean isActive;
	public ItemStack itemStack;

	public SyncPedestalPKT() {}

	public SyncPedestalPKT(DMPedestalTile tile)
	{
		pos = tile.getPos();
		isActive = tile.getActive();
		itemStack = tile.getItemStack();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		isActive = buf.readBoolean();
		itemStack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeBoolean(isActive);
		ByteBufUtils.writeItemStack(buf, itemStack);
	}

	public static class Handler implements IMessageHandler<SyncPedestalPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SyncPedestalPKT message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() -> {
                TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(message.pos);

                if (te instanceof DMPedestalTile)
                {
                    DMPedestalTile pedestal = ((DMPedestalTile) te);
                    pedestal.setActive(message.isActive);
                    pedestal.setInventorySlotContents(0, message.itemStack);
                }
            });

			return null;
		}
	}
}
