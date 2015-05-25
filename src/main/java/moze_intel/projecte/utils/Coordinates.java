package moze_intel.projecte.utils;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class Coordinates
{
	public int x;
	public int y;
	public int z;
	
	public Coordinates(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Coordinates(TileEntity tile)
	{
		this.x = tile.xCoord;
		this.y = tile.yCoord;
		this.z = tile.zCoord;
	}
	
	public Coordinates(Entity ent)
	{
		this.x = (int) ent.posX;
		this.y = (int) ent.posY;
		this.z = (int) ent.posZ;
	}
	
	public Coordinates(MovingObjectPosition mop)
	{
		this.x = mop.blockX;
		this.y = mop.blockY;
		this.z = mop.blockZ;
	}
	
	@Override
	public int hashCode()
	{
		return toString().hashCode();
	}
	
	@Override
	public String toString()
	{
		return x + ":" + y + ":" + z;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof Coordinates))
		{
			return false;
		}
		
		Coordinates c = (Coordinates) other;
		
		return (x == c.x && y == c.y && z == c.z);
	}
}
