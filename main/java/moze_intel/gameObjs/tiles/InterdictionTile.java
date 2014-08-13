package moze_intel.gameObjs.tiles;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IProjectile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class InterdictionTile extends TileEntity
{
	AxisAlignedBB bBox = null;
	int tickCounter = 0;
	
	
	public void updateEntity()
	{
		/*if (worldObj.isRemote) 
		{
			return;
		}*/
		
		if (bBox == null)
		{
			bBox = AxisAlignedBB.getBoundingBox(xCoord - 8, yCoord - 8, zCoord - 8, xCoord + 8, yCoord + 8, zCoord + 8);
		}
		
		if (tickCounter != 2)
		{
			tickCounter++;
			return;
		}
		
		List<Entity> list = worldObj.getEntitiesWithinAABB(Entity.class, bBox);
		
		for (Entity ent : list)
		{
			if ((ent instanceof EntityLiving) || (ent instanceof IProjectile))
			{
			
				Vec3 p = Vec3.createVectorHelper(xCoord, yCoord, zCoord);
				Vec3 t = Vec3.createVectorHelper(ent.posX, ent.posY, ent.posZ);
				double distance = p.distanceTo(t) + 0.1D;

				Vec3 r = Vec3.createVectorHelper(t.xCoord - p.xCoord, t.yCoord - p.yCoord, t.zCoord - p.zCoord);

				ent.motionX += r.xCoord / 1.5D / distance;
				ent.motionY += r.yCoord / 1.5D / distance;
				ent.motionZ += r.zCoord / 1.5D / distance;
			}
		}
		
		tickCounter = 0;
	}
}
