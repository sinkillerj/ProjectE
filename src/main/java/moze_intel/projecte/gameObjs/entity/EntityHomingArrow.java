package moze_intel.projecte.gameObjs.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

public class EntityHomingArrow extends EntityArrow
{
	EntityLiving target;
	World world;

	private void init(World world)
	{
		rng = new Random();
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
		
		if (target == null)
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
		else if (!isInGround())
		{
			world.spawnParticle("flame", box.maxX, box.maxY, box.maxZ, 0.0D, 0.0D, 0.0D);
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
	
	private boolean isInGround()
	{
		boolean result = false;
		Field field = EntityArrow.class.getDeclaredFields()[5];
		field.setAccessible(true);

		try 
		{
			result = field.getBoolean(this);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
}
