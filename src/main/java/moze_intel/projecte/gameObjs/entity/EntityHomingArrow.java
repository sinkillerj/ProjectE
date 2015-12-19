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
		boolean inGround = WorldHelper.isArrowInGround(this);
		if (!worldObj.isRemote)
		{
			if (hasTarget() && (!getTarget().isEntityAlive() || inGround))
			{
				dataWatcher.updateObject(DW_TARGET_ID, NO_TARGET);
				PELogger.logInfo("Removing target");
			}

			if (!hasTarget() && !inGround && newTargetCooldown <= 0)
			{
				PELogger.logInfo("Finding new target");
				findNewTarget();
			} else
			{
				newTargetCooldown--;
			}
		}

		if (ticksExisted > 3 && hasTarget() && !WorldHelper.isArrowInGround(this))
		{
			AxisAlignedBB box = this.boundingBox;
			worldObj.spawnParticle("flame", box.maxX, box.maxY, box.maxZ, 0.0D, 0.0D, 0.0D);
			setIsCritical(true);
			Entity target = getTarget();


			Vector3d arrowLoc = new Vector3d(posX, posY, posZ);
			Vector3d targetLoc = new Vector3d(target.posX, target.boundingBox.minY + target.height, target.posZ);
			Vector3d lookVec = new Vector3d(targetLoc);
			lookVec.sub(arrowLoc);

			// double dotProduct = new Vector3d(arrowLoc).dot(targetLoc);
			double theta = arrowLoc.angle(lookVec); //Math.acos(dotProduct / (arrowLoc.length() * targetLoc.length()));

			Vector3d crossProduct = new Vector3d();
			crossProduct.cross(arrowLoc, targetLoc);
			crossProduct.normalize();

			Matrix4d transform = new Matrix4d();
			transform.set(new AxisAngle4d(crossProduct, theta * 0.65));

			Vector3d adjustedLookVec = new Vector3d(lookVec);
			transform.transform(lookVec, adjustedLookVec);

			setThrowableHeading(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z, 1.3F, 0);
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

	private void updateHeading(double velocityX, double velocityY, double velocityZ)
	{
		float f3 = MathHelper.sqrt_double(velocityX * velocityX + velocityZ * velocityZ);
		this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(velocityX, velocityZ) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(velocityY, f3) * 180.0D / Math.PI);
	}

	private double getPitch(Vector3d vec)
	{
		return Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z));
	}

	private double getYaw(Vector3d vec)
	{
		return Math.atan2(vec.z, vec.x) - Math.PI / 2.0D;
	}

	private double toDegrees(double radian)
	{
		return radian * 180.0 / Math.PI;
	}

	private void setThrowableHeadingCopy(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_)
	{
		float f2 = MathHelper.sqrt_double(p_70186_1_ * p_70186_1_ + p_70186_3_ * p_70186_3_ + p_70186_5_ * p_70186_5_);
		p_70186_1_ /= (double)f2;
		p_70186_3_ /= (double)f2;
		p_70186_5_ /= (double)f2;
		p_70186_1_ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)p_70186_8_;
		p_70186_3_ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)p_70186_8_;
		p_70186_5_ += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)p_70186_8_;
		p_70186_1_ *= (double)p_70186_7_;
		p_70186_3_ *= (double)p_70186_7_;
		p_70186_5_ *= (double)p_70186_7_;
//		this.motionX = p_70186_1_;
//		this.motionY = p_70186_3_;
//		this.motionZ = p_70186_5_;
		float f3 = MathHelper.sqrt_double(p_70186_1_ * p_70186_1_ + p_70186_5_ * p_70186_5_);
		this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(p_70186_1_, p_70186_5_) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(p_70186_3_, (double)f3) * 180.0D / Math.PI);
	}
}
