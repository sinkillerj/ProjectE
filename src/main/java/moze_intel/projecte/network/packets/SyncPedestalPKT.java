package moze_intel.projecte.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class SyncPedestalPKT implements IMessage
{
	public int x, y, z;
	public boolean isActive;
	public ItemStack itemStack;

	public SyncPedestalPKT() {}

	public SyncPedestalPKT(DMPedestalTile tile)
	{
		x = tile.xCoord;
		y = tile.yCoord;
		z = tile.zCoord;
		isActive = tile.getActive();
		itemStack = tile.getItemStack();
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		isActive = buf.readBoolean();
		itemStack = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeBoolean(isActive);
		ByteBufUtils.writeItemStack(buf, itemStack);
	}

	public static class Handler implements IMessageHandler<SyncPedestalPKT, IMessage>
	{
		@Override
		public IMessage onMessage(final SyncPedestalPKT message, MessageContext ctx)
		{
			TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(message.x, message.y, message.z);

			if (te instanceof DMPedestalTile)
			{
				DMPedestalTile pedestal = ((DMPedestalTile) te);
				pedestal.setActive(message.isActive);
				pedestal.setInventorySlotContents(0, message.itemStack);
			}

			return null;
		}
	}
}
