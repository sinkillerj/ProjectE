package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class EntityHomingArrow extends EntityArrow
{
	EntityLiving target;
	World world;

	private void init(World world)
	{
		this.world = world;
	}

	public EntityHomingArrow(World world)
	{
		super(world);
		this.canBePickedUp = 0;
		init(world);
	}

	public EntityHomingArrow(World world, EntityLivingBase par2, float par3) 
	{
		super(world, par2, par3);
		this.canBePickedUp = 0;
		init(world);
	}
	
	@Override
	public void onEntityUpdate()
	{
		//TODO Create proper custom arrow. This one is duplicating because of the super call for onUpdate();
		//^^ The above is probably fixed. Probably.
		//TODO: Create a more Visually appeasing arc
		super.onEntityUpdate();

		AxisAlignedBB box = this.boundingBox;
		
		if (target == null && !WorldHelper.isArrowInGround(this))
		{
			AxisAlignedBB bBox = box.expand(8, 8, 8);
			List<EntityLiving> list = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, bBox);

			double distance = 100000;

			double disToEnt1 = 1000;
			
			for (EntityLiving entity : list)
			{
				double toIt = distanceTo(entity);

				double disToEnt2 = distanceTo(entity);
				
				if(disToEnt2 < disToEnt1){
					disToEnt1 = disToEnt2;
					target = entity;
				}
			}
			
			if (target == null)
			{
				return;
			}

			double xyz[] = checkDistance(target, this.posX, this.posY, this.posZ);
			
			double x = xyz[0];
			double y = xyz[1];
			double z = xyz[2];
			
			this.setThrowableHeading(x, y, z, 2.0F, 0.0F);
		}
		else if (!WorldHelper.isArrowInGround(this))
		{
			if (target.getHealth() == 0) 
			{
				target = null;
				return;
			}

			world.spawnParticle("flame", box.maxX, box.maxY, box.maxZ, 0.0D, 0.0D, 0.0D);

			double[] yawPitch = getYawPitch(target, this);
			
			this.setPositionAndRotation(target.posX, target.posY, target.posZ, (float)yawPitch[0], (float)yawPitch[1]);
			
			double xyz[] = checkDistance(target, this.posX, this.posY, this.posZ);
			
			double x = xyz[0];
			double y = xyz[1];
			double z = xyz[2];
			
			this.setThrowableHeading(x, y, z, 2.0F, 0.0F);
		}
	}

	private double distanceTo(EntityLiving entity)
	{
		double [] ds = new double []
		{
			this.posX - entity.posX,
			this.posY - entity.posY,
			this.posZ - entity.posZ
		};

		double d = 0;

		for (int i = 0; i < 3; i++)
			d += ds[i] * ds[i];

		return Math.sqrt(d);
	}
	
	private double[] checkDistance(EntityLiving entity, double x, double y, double z){
		double rX, rY, rZ;
		
		rX = x;
		rY = y;
		rZ = z;
		
		if(entity.posX != x){
			if(entity.posX > x){
				rX =+ entity.posX;
			} else{
				rX--;
			}
		}
		
		if(entity.posY != y){
			if(entity.posY > y){
				rY =+ entity.posY;
			}
		}
		
		if(entity.posZ != z){
			if(entity.posZ > z){
				rZ =+ entity.posZ;
			} else{
				rZ--;
			}
		}
		
		double rValues[] = {rX, rY, rZ};
		
		return rValues;
	}
	
	private double[] getYawPitch(EntityLiving target, EntityArrow arrow){
		double dx, dy, dz;
		
		if(target.posX > arrow.posX){
			dx = target.posX - arrow.posX;
		} else{
			dx = arrow.posX - target.posX;
		}
		
		if(target.posY > arrow.posY){
			dy = target.posY - arrow.posY;
		} else{
			dy = arrow.posY - target.posY;
		}
		
		if(target.posZ > arrow.posZ){
			dz = target.posZ - arrow.posZ;
		} else{
			dz = arrow.posZ - target.posZ;
		}
		
		double pitch = Math.atan2(dy,Math.sqrt(dx*dx+dz*dz));
		double yaw = Math.atan2(dz,dx)+180;
		
		double[] rValues = {yaw, pitch};
		
		return rValues;
	}
}
