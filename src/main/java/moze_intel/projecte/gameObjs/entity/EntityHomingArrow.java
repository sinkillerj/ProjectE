package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
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
		dataWatcher.addObject(DW_TARGET_ID, NO_TARGET); // Target entity id
	}

	@Override
	public void onUpdate()
	{
		onEntityUpdate();

		this.canBePickedUp = 0;

		boolean inGround = WorldHelper.isArrowInGround(this);
		if (!worldObj.isRemote && this.ticksExisted > 3)
		{
			if (hasTarget() && (!getTarget().isEntityAlive() || inGround))
			{
				dataWatcher.updateObject(DW_TARGET_ID, NO_TARGET);
			}

			if (!hasTarget() && !inGround && newTargetCooldown <= 0)
			{
				findNewTarget();
			} else
			{
				newTargetCooldown--;
			}
		}

		if (ticksExisted > 3 && hasTarget() && !WorldHelper.isArrowInGround(this))
		{
			this.worldObj.spawnParticle("flame", this.posX + this.motionX / 4.0D, this.posY + this.motionY / 4.0D, this.posZ + this.motionZ / 4.0D, -this.motionX / 2, -this.motionY / 2 + 0.2D, -this.motionZ / 2);
			this.worldObj.spawnParticle("flame", this.posX + this.motionX / 4.0D, this.posY + this.motionY / 4.0D, this.posZ + this.motionZ / 4.0D, -this.motionX / 2, -this.motionY / 2 + 0.2D, -this.motionZ / 2);
			Entity target = getTarget();


			Vector3d arrowLoc = new Vector3d(posX, posY, posZ);
			Vector3d targetLoc = new Vector3d(target.posX, target.boundingBox.minY + target.height, target.posZ);

			// Get the vector that points straight from the arrow to the target
			Vector3d lookVec = new Vector3d(targetLoc);
			lookVec.sub(arrowLoc);

			Vector3d arrowMotion = new Vector3d(this.motionX, this.motionY, this.motionZ);

			// Find the angle between the direct vec and arrow vec, and then clamp it so it arcs a bit
			double theta = wrap180Radian(arrowMotion.angle(lookVec));
			theta = clampAbs(theta, Math.PI / 2); // Dividing by higher numbers kills accuracy

			// Find the cross product to determine the axis of rotation
			Vector3d crossProduct = new Vector3d();
			crossProduct.cross(arrowMotion, lookVec);
			crossProduct.normalize();

			// Create the rotation using the axis and our angle
			Matrix4d transform = new Matrix4d();
			transform.set(new AxisAngle4d(crossProduct, theta));

			// Adjust the vector
			Vector3d adjustedLookVec = new Vector3d(arrowMotion);
			transform.transform(arrowMotion, adjustedLookVec);

			// Tell mc to adjust our rotation accordingly
			setThrowableHeading(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z, 1.0F, 0);
			super.onUpdate();

//			old homing code (sucks)
//			double d5 = target.posX - this.posX;
//			double d6 = target.boundingBox.minY + target.height - this.posY;
//			double d7 = target.posZ - this.posZ;
//
//			this.setThrowableHeading(d5, d6, d7, 0.1F, 0.0F);
//			super.onUpdate();
		} else
		{
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

	private double wrap180Radian(double radian)
	{
		radian %= 2 * Math.PI;

		while (radian >= Math.PI)
		{
			radian -= 2 * Math.PI;
		}

		while (radian < -Math.PI)
		{
			radian += 2 * Math.PI;
		}

		return radian;
	}

	private double clampAbs(double param, double maxMagnitude)
	{
		if (Math.abs(param) > maxMagnitude)
		{
			//System.out.println("CLAMPED");
			if (param < 0)
			{
				param = -Math.abs(maxMagnitude);
			} else
			{
				param = Math.abs(maxMagnitude);
			}
		}

		return param;
	}
}
