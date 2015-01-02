package moze_intel.projecte.gameObjs.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.List;

public class EntityHomingArrow extends EntityArrow
{
	public EntityHomingArrow(World world)
	{
		super(world);
	}

	public EntityHomingArrow(World world, EntityLivingBase par2, float par3) 
	{
		super(world, par2, par3);
	}
	
	@Override
	public void onUpdate()
	{
		//TODO Create proper custom arrow. This one is duplicating because of the super call for onUpdate();
		super.onUpdate();
		
		if (!isInGround())
		{
			AxisAlignedBB bBox = this.boundingBox.expand(16, 16, 16);
			List<EntityLiving> list = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, bBox);
			
			if (list.size() <= 0)
			{
				return;
			}
			
			EntityLiving target = list.get(0);
			
			double d5 = target.posX - this.posX;
			double d6 = target.boundingBox.minY + (double)(target.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
			double d7 = target.posZ - this.posZ;
			
			this.setThrowableHeading(d5, d6, d7, 2.0F, 0.0F);
			this.setVelocity(d5, d6, d7);
		}
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
