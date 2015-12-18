package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EntityHomingArrow extends EntityArrow
{
	private static final int DW_TARGET_ID = 31;
	private static final int NO_TARGET = -1;

	private int newTargetCooldown = 0;

	public EntityHomingArrow(World world)
	{
		super(world);
	}

	public EntityHomingArrow(World world, EntityLivingBase par2, float par3) 
	{
		super(world, par2, par3);
	}

	@Override
	public void entityInit()
	{
		super.entityInit();
		setIsCritical(true);
		dataWatcher.addObject(DW_TARGET_ID, NO_TARGET); // Target entity id
	}

	@Override
	public void onUpdate()
	{
		if (!worldObj.isRemote)
		{
			if (hasTarget() && !getTarget().isEntityAlive())
			{
				dataWatcher.updateObject(DW_TARGET_ID, NO_TARGET);
				PELogger.logInfo("Removing dead target");
			}

			if (!hasTarget() && !WorldHelper.isArrowInGround(this) && newTargetCooldown <= 0)
			{
				PELogger.logInfo("Finding new target");
				findNewTarget();
			} else
			{
				newTargetCooldown--;
			}
		}

		if (!hasTarget())
		{
			super.onUpdate();
			return;
		}

		AxisAlignedBB box = this.boundingBox;
		
		if (!WorldHelper.isArrowInGround(this))
		{
			worldObj.spawnParticle("flame", box.maxX, box.maxY, box.maxZ, 0.0D, 0.0D, 0.0D);
			Entity target = getTarget();
			double d5 = target.posX - this.posX;
			double d6 = target.boundingBox.minY + target.height - this.posY;
			double d7 = target.posZ - this.posZ;
			
			this.setThrowableHeading(d5, d6, d7, 0.1F, 0.0F);
			super.onUpdate();
		}
	}

	private void findNewTarget()
	{
		List<EntityLiving> candidates = worldObj.getEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(8, 8, 8));
		Collections.sort(candidates, new Comparator<EntityLiving>() {
			@Override
			public int compare(EntityLiving o1, EntityLiving o2) {
				double dist = EntityHomingArrow.this.getDistanceSqToEntity(o1) - EntityHomingArrow.this.getDistanceSqToEntity(o2);
				if (dist == 0.0)
				{
					return 0;
				} else
				{
					return dist > 0.0 ? 1 : -1;
				}
			}
		});

		if (!candidates.isEmpty())
		{
			dataWatcher.updateObject(DW_TARGET_ID, candidates.get(0).getEntityId());
			PELogger.logInfo("Found new target");
		}

		newTargetCooldown = 5;
	}

	private EntityLiving getTarget()
	{
		return ((EntityLiving) worldObj.getEntityByID(dataWatcher.getWatchableObjectInt(DW_TARGET_ID)));
	}

	private boolean hasTarget()
	{
		return getTarget() != null;
	}
}
