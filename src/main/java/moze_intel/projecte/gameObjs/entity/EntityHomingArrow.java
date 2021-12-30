package moze_intel.projecte.gameObjs.entity;

import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

public class EntityHomingArrow extends Arrow {

	private static final EntityDataAccessor<Integer> DW_TARGET_ID = SynchedEntityData.defineId(EntityHomingArrow.class, EntityDataSerializers.INT);
	private static final int NO_TARGET = -1;

	private int newTargetCooldown = 0;

	public EntityHomingArrow(EntityType<EntityHomingArrow> type, Level level) {
		super(type, level);
	}

	public EntityHomingArrow(Level level, LivingEntity shooter, float damage) {
		super(level, shooter);
		this.setBaseDamage(damage);
		this.pickup = Pickup.CREATIVE_ONLY;
	}

	@Nonnull
	@Override
	public EntityType<?> getType() {
		return PEEntityTypes.HOMING_ARROW.get();
	}

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(DW_TARGET_ID, NO_TARGET); // Target entity id
	}

	@Override
	protected void doPostHurtEffects(@Nonnull LivingEntity living) {
		super.doPostHurtEffects(living);
		// Strip damage vulnerability
		living.invulnerableTime = 0;
	}

	@Override
	public void tick() {
		if (!level.isClientSide && this.tickCount > 3) {
			if (hasTarget() && (!getTarget().isAlive() || this.inGround)) {
				entityData.set(DW_TARGET_ID, NO_TARGET);
			}

			if (!hasTarget() && !this.inGround && newTargetCooldown <= 0) {
				findNewTarget();
			} else {
				newTargetCooldown--;
			}
		}

		if (tickCount > 3 && hasTarget() && !this.inGround) {
			double mX = getDeltaMovement().x();
			double mY = getDeltaMovement().y();
			double mZ = getDeltaMovement().z();
			this.getCommandSenderWorld().addParticle(ParticleTypes.FLAME, getX() + mX / 4.0D, getY() + mY / 4.0D, getZ() + mZ / 4.0D, -mX / 2, -mY / 2 + 0.2D, -mZ / 2);
			this.getCommandSenderWorld().addParticle(ParticleTypes.FLAME, getX() + mX / 4.0D, getY() + mY / 4.0D, getZ() + mZ / 4.0D, -mX / 2, -mY / 2 + 0.2D, -mZ / 2);
			Entity target = getTarget();

			Vec3 arrowLoc = new Vec3(getX(), getY(), getZ());
			Vec3 targetLoc = new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ());

			// Get the vector that points straight from the arrow to the target
			Vec3 lookVec = targetLoc.subtract(arrowLoc);

			Vec3 arrowMotion = new Vec3(mX, mY, mZ);

			// Find the angle between the direct vec and arrow vec, and then clamp it so it arcs a bit
			double theta = wrap180Radian(angleBetween(arrowMotion, lookVec));
			theta = clampAbs(theta, Math.PI / 2); // Dividing by higher numbers kills accuracy

			// Find the cross product to determine the axis of rotation
			Vec3 crossProduct = arrowMotion.cross(lookVec).normalize();

			// Create the rotation using the axis and our angle and adjust the vector to it
			Vec3 adjustedLookVec = transform(crossProduct, theta, arrowMotion);

			// Tell mc to adjust our rotation accordingly
			shoot(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z, 1.0F, 0);
		}

		super.tick();
	}

	private Vec3 transform(Vec3 axis, double angle, Vec3 normal) {
		//Trimmed down math of javax vecmath calculations, potentially should be rewritten at some point
		double m00 = 1;
		double m01 = 0;
		double m02 = 0;

		double m10 = 0;
		double m11 = 1;
		double m12 = 0;

		double m20 = 0;
		double m21 = 0;
		double m22 = 1;
		double mag = Math.sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z);
		if (mag >= 1.0E-10) {
			mag = 1.0 / mag;
			double ax = axis.x * mag;
			double ay = axis.y * mag;
			double az = axis.z * mag;

			double sinTheta = Math.sin(angle);
			double cosTheta = Math.cos(angle);
			double t = 1.0 - cosTheta;

			double xz = ax * az;
			double xy = ax * ay;
			double yz = ay * az;

			m00 = t * ax * ax + cosTheta;
			m01 = t * xy - sinTheta * az;
			m02 = t * xz + sinTheta * ay;

			m10 = t * xy + sinTheta * az;
			m11 = t * ay * ay + cosTheta;
			m12 = t * yz - sinTheta * ax;

			m20 = t * xz - sinTheta * ay;
			m21 = t * yz + sinTheta * ax;
			m22 = t * az * az + cosTheta;
		}
		return new Vec3(m00 * normal.x + m01 * normal.y + m02 * normal.z,
				m10 * normal.x + m11 * normal.y + m12 * normal.z,
				m20 * normal.x + m21 * normal.y + m22 * normal.z);
	}

	@Nonnull
	@Override
	protected ItemStack getPickupItem() {
		return new ItemStack(Items.ARROW);
	}

	private void findNewTarget() {
		List<Mob> candidates = level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(8, 8, 8));

		if (!candidates.isEmpty()) {
			candidates.sort(Comparator.comparing(EntityHomingArrow.this::distanceToSqr, Double::compare));
			entityData.set(DW_TARGET_ID, candidates.get(0).getId());
		}

		newTargetCooldown = 5;
	}

	private Mob getTarget() {
		return (Mob) level.getEntity(entityData.get(DW_TARGET_ID));
	}

	private boolean hasTarget() {
		return getTarget() != null;
	}

	private double angleBetween(Vec3 v1, Vec3 v2) {
		double vDot = v1.dot(v2) / (v1.length() * v2.length());
		if (vDot < -1.0) {
			vDot = -1.0;
		}
		if (vDot > 1.0) {
			vDot = 1.0;
		}
		return Math.acos(vDot);
	}

	private double wrap180Radian(double radian) {
		radian %= 2 * Math.PI;
		while (radian >= Math.PI) {
			radian -= 2 * Math.PI;
		}
		while (radian < -Math.PI) {
			radian += 2 * Math.PI;
		}
		return radian;
	}

	private double clampAbs(double param, double maxMagnitude) {
		if (Math.abs(param) > maxMagnitude) {
			//System.out.println("CLAMPED");
			if (param < 0) {
				param = -Math.abs(maxMagnitude);
			} else {
				param = Math.abs(maxMagnitude);
			}
		}
		return param;
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}
}