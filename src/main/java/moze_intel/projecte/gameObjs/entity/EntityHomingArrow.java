package moze_intel.projecte.gameObjs.entity;

import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityHomingArrow extends ArrowEntity {

	private static final DataParameter<Integer> DW_TARGET_ID = EntityDataManager.createKey(EntityHomingArrow.class, DataSerializers.VARINT);
	private static final int NO_TARGET = -1;

	private int newTargetCooldown = 0;

	public EntityHomingArrow(EntityType<EntityHomingArrow> type, World world) {
		super(type, world);
	}

	public EntityHomingArrow(World world, LivingEntity shooter, float damage) {
		super(world, shooter);
		this.setDamage(damage);
		this.pickupStatus = PickupStatus.CREATIVE_ONLY;
	}

	@Nonnull
	@Override
	public EntityType<?> getType() {
		return ObjHandler.HOMING_ARROW;
	}

	@Override
	public void registerData() {
		super.registerData();
		dataManager.register(DW_TARGET_ID, NO_TARGET); // Target entity id
	}

	@Override
	protected void arrowHit(@Nonnull LivingEntity living) {
		super.arrowHit(living);
		// Strip damage vulnerability
		living.hurtResistantTime = 0;
	}

	@Override
	public void tick() {
		if (!world.isRemote && this.ticksExisted > 3) {
			if (hasTarget() && (!getTarget().isAlive() || this.inGround)) {
				dataManager.set(DW_TARGET_ID, NO_TARGET);
			}

			if (!hasTarget() && !this.inGround && newTargetCooldown <= 0) {
				findNewTarget();
			} else {
				newTargetCooldown--;
			}
		}

		if (ticksExisted > 3 && hasTarget() && !this.inGround) {
			double mX = getMotion().getX();
			double mY = getMotion().getY();
			double mZ = getMotion().getZ();
			this.getEntityWorld().addParticle(ParticleTypes.FLAME, getPosX() + mX / 4.0D, getPosY() + mY / 4.0D, getPosZ() + mZ / 4.0D, -mX / 2, -mY / 2 + 0.2D, -mZ / 2);
			this.getEntityWorld().addParticle(ParticleTypes.FLAME, getPosX() + mX / 4.0D, getPosY() + mY / 4.0D, getPosZ() + mZ / 4.0D, -mX / 2, -mY / 2 + 0.2D, -mZ / 2);
			Entity target = getTarget();

			Vec3d arrowLoc = new Vec3d(getPosX(), getPosY(), getPosZ());
			Vec3d targetLoc = new Vec3d(target.getPosX(), target.getPosY() + target.getHeight() / 2, target.getPosZ());

			// Get the vector that points straight from the arrow to the target
			Vec3d lookVec = targetLoc.subtract(arrowLoc);

			Vec3d arrowMotion = new Vec3d(mX, mY, mZ);

			// Find the angle between the direct vec and arrow vec, and then clamp it so it arcs a bit
			double theta = wrap180Radian(angleBetween(arrowMotion, lookVec));
			theta = clampAbs(theta, Math.PI / 2); // Dividing by higher numbers kills accuracy

			// Find the cross product to determine the axis of rotation
			Vec3d crossProduct = arrowMotion.crossProduct(lookVec).normalize();

			// Create the rotation using the axis and our angle and adjust the vector to it
			Vec3d adjustedLookVec = transform(crossProduct, theta, arrowMotion);

			// Tell mc to adjust our rotation accordingly
			shoot(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z, 1.0F, 0);
		}

		super.tick();
	}

	private Vec3d transform(Vec3d axis, double angle, Vec3d normal) {
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
		return new Vec3d(m00 * normal.x + m01 * normal.y + m02 * normal.z,
				m10 * normal.x + m11 * normal.y + m12 * normal.z,
				m20 * normal.x + m21 * normal.y + m22 * normal.z);
	}

	@Nonnull
	@Override
	protected ItemStack getArrowStack() {
		return new ItemStack(Items.ARROW);
	}

	private void findNewTarget() {
		List<MobEntity> candidates = world.getEntitiesWithinAABB(MobEntity.class, this.getBoundingBox().grow(8, 8, 8));

		if (!candidates.isEmpty()) {
			candidates.sort(Comparator.comparing(EntityHomingArrow.this::getDistanceSq, Double::compare));
			dataManager.set(DW_TARGET_ID, candidates.get(0).getEntityId());
		}

		newTargetCooldown = 5;
	}

	private MobEntity getTarget() {
		return (MobEntity) world.getEntityByID(dataManager.get(DW_TARGET_ID));
	}

	private boolean hasTarget() {
		return getTarget() != null;
	}

	private double angleBetween(Vec3d v1, Vec3d v2) {
		double vDot = v1.dotProduct(v2) / (v1.length() * v2.length());
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
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}