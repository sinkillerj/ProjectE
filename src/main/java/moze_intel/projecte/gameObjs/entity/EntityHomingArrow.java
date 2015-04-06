package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.ReflectionHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
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
		init(world);
	}

	public EntityHomingArrow(World world, EntityLivingBase par2, float par3) 
	{
		super(world, par2, par3);
		init(world);
	}
	
	@Override
	public void onUpdate()
	{
		//TODO Create proper custom arrow. This one is duplicating because of the super call for onUpdate();
		super.onUpdate();

		AxisAlignedBB box = this.boundingBox;
		
		if (target == null && !WorldHelper.isArrowInGround(this))
		{
			AxisAlignedBB bBox = box.expand(8, 8, 8);
			List<EntityLiving> list = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, bBox);

			double distance = 100000;

			for (EntityLiving entity : list)
			{
				double toIt = distanceTo(entity);

				if (distance > toIt)
				{
					distance = toIt;
					target = entity;
				}
			}
			
			if (target == null)
			{
				return;
			}

			double d5 = target.posX - this.posX;
			double d6 = target.boundingBox.minY + target.height - this.posY;
			double d7 = target.posZ - this.posZ;
			
			this.setThrowableHeading(d5, d6, d7, 2.0F, 0.0F);
		}
		else if (!WorldHelper.isArrowInGround(this))
		{
			if (target.getHealth() == 0) 
			{
				target = null;
				return;
			}

			world.spawnParticle("flame", box.maxX, box.maxY, box.maxZ, 0.0D, 0.0D, 0.0D);

			double d5 = target.posX - this.posX;
			double d6 = target.boundingBox.minY + target.height - this.posY;
			double d7 = target.posZ - this.posZ;
			
			this.setThrowableHeading(d5, d6, d7, 2.0F, 0.0F);
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
}
