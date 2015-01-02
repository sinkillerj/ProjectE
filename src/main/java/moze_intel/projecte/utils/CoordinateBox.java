package moze_intel.projecte.utils;

import net.minecraft.util.AxisAlignedBB;

/**
 * Utility class similar to AABB, but doesn't register in the AABBpool 
 */

public class CoordinateBox
{
	public double minX;
	public double minY;
	public double minZ;
	public double maxX;
	public double maxY;
	public double maxZ;
	
	public CoordinateBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public CoordinateBox(AxisAlignedBB box)
	{
		minX = box.minX;
		minY = box.minY;
		minZ = box.minZ;
		maxX = box.maxX;
		maxY = box.maxY;
		maxZ = box.maxZ;
	}
	
	public void expand(double x, double y, double z)
	{
		this.minX -= x;
		this.minY -= y;
		this.minZ -= z;
		this.maxX += x;
		this.maxY += y;
		this.maxZ += z;
	}
	
	public void offset(double x, double y, double z)
	{
		this.minX += x;
		this.maxX += x;
		this.maxY += y;
		this.minY += y;
		this.minZ += z;
		this.maxZ += z;
	}
	
	@Override
	public String toString()
	{
		return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
	}
}
