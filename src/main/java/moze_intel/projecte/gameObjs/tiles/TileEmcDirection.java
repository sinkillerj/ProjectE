package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.OrientationSyncPKT;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;

// TODO 1.8 TE's really should be using the blockstate too for rotation...this may go away as it exists only for rendering (?!)
public abstract class TileEmcDirection extends TileEmc
{
	private EnumFacing orientation;
	
	public TileEmcDirection()
	{
		this.orientation = EnumFacing.SOUTH;
	}
	
	public EnumFacing getOrientation()
	{
		return orientation;
	}

	public void setOrientation(EnumFacing orientation)
	{
		this.orientation = orientation;
	}

	public void setOrientation(int orientation)
	{
		this.orientation = EnumFacing.getFront(orientation);
	}
	
	public void setRelativeOrientation(EntityLivingBase ent, boolean sendPacket)
	{
		EnumFacing direction = ent.getHorizontalFacing();
		setOrientation(direction);
		
		if (sendPacket)
		{
			PacketHandler.sendToAll(new OrientationSyncPKT(this, direction.getIndex()));
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound)
	{
		super.readFromNBT(nbtTagCompound);

		if (nbtTagCompound.hasKey("Direction"))
		{
			this.orientation = EnumFacing.getFront(nbtTagCompound.getByte("Direction"));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound)
	{
		super.writeToNBT(nbtTagCompound);

		nbtTagCompound.setByte("Direction", (byte) orientation.getIndex());
	}
	
	@Override
	public Packet getDescriptionPacket() 
	{
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(pos, 0, tag);
	}
		
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) 
	{
		this.readFromNBT(packet.getNbtCompound());
	}
}
