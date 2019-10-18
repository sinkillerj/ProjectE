package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import java.util.Comparator;
import java.util.List;

public class EntityHomingArrow extends ArrowEntity
{
	private static final DataParameter<Integer> DW_TARGET_ID = EntityDataManager.createKey(EntityHomingArrow.class, DataSerializers.VARINT);
	private static final int NO_TARGET = -1;

	private int newTargetCooldown = 0;

	public EntityHomingArrow(EntityType<EntityHomingArrow> type, World world)
	{
		super(type, world);
	}

	public EntityHomingArrow(World world, LivingEntity shooter, float damage)
	{
		super(world, shooter);
		this.setDamage(damage);
		this.pickupStatus = PickupStatus.CREATIVE_ONLY;
	}

	@Nonnull
	@Override
	public EntityType<?> getType()
	{
		return ObjHandler.HOMING_ARROW;
	}

	@Override
	public void registerData()
	{
		super.registerData();
		dataManager.register(DW_TARGET_ID, NO_TARGET); // Target entity id
	}

	@Override
	protected void arrowHit(@Nonnull LivingEntity living)
	{
		super.arrowHit(living);
		// Strip damage vulnerability
		living.hurtResistantTime = 0;
	}

	@Override
	public void tick()
	{
		if (!world.isRemote && this.ticksExisted > 3)
		{
			if (hasTarget() && (!getTarget().isAlive() || this.inGround))
			{
				dataManager.set(DW_TARGET_ID, NO_TARGET);
			}

			if (!hasTarget() && !this.inGround && newTargetCooldown <= 0)
			{
				findNewTarget();
			} else
			{
				newTargetCooldown--;
			}
		}

		if (ticksExisted > 3 && hasTarget() && !this.inGround)
		{
			double mX = getMotion().getX();
			double mY = getMotion().getY();
			double mZ = getMotion().getZ();
			this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.posX + mX / 4.0D, this.posY + mY / 4.0D, this.posZ + mZ / 4.0D, -mX / 2, -mY / 2 + 0.2D, -mZ / 2);
			this.getEntityWorld().addParticle(ParticleTypes.FLAME, this.posX + mX / 4.0D, this.posY + mY / 4.0D, this.posZ + mZ / 4.0D, -mX / 2, -mY / 2 + 0.2D, -mZ / 2);
			Entity target = getTarget();


			Vector3d arrowLoc = new Vector3d(posX, posY, posZ);
			Vector3d targetLoc = new Vector3d(target.posX, target.posY + target.getHeight() / 2, target.posZ);

			// Get the vector that points straight from the arrow to the target
			Vector3d lookVec = new Vector3d(targetLoc);
			lookVec.sub(arrowLoc);

			Vector3d arrowMotion = new Vector3d(mX, mY, mZ);

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
			shoot(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z, 1.0F, 0);
		}

		super.tick();
	}

	@Nonnull
	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(Items.ARROW);
	}

	private void findNewTarget()
	{
		List<MobEntity> candidates = world.getEntitiesWithinAABB(MobEntity.class, this.getBoundingBox().grow(8, 8, 8));

		if (!candidates.isEmpty())
		{
			candidates.sort(Comparator.comparing(EntityHomingArrow.this::getDistanceSq, Double::compare));
			dataManager.set(DW_TARGET_ID, candidates.get(0).getEntityId());
		}

		newTargetCooldown = 5;
	}

	private MobEntity getTarget()
	{
		return ((MobEntity) world.getEntityByID(dataManager.get(DW_TARGET_ID)));
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
