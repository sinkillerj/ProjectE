package moze_intel.projecte.gameObjs.tiles;

import moze_intel.projecte.gameObjs.blocks.BlockDirection;
import moze_intel.projecte.network.PacketHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

// TODO 1.8++ nuke this entirely. All rotations handled in metadata now.
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
		setOrientation(ent.getHorizontalFacing());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound)
	{
		super.readFromNBT(nbtTagCompound);

		if (nbtTagCompound.hasKey("Direction"))
		{
			this.orientation = EnumFacing.getFront(nbtTagCompound.getByte("Direction"));
			if (worldObj != null)
			{
				worldObj.setBlockState(pos, this.getBlockType().getDefaultState().withProperty(BlockDirection.FACING, orientation));
			}
			nbtTagCompound.removeTag("Direction");
		}
	}
}
