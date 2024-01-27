package moze_intel.projecte.gameObjs.entity;

import java.util.Comparator;
import java.util.List;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class EntityHomingArrow extends Arrow {

	private static final EntityDataAccessor<Integer> DW_TARGET_ID = SynchedEntityData.defineId(EntityHomingArrow.class, EntityDataSerializers.INT);
	//Dividing by higher numbers kills accuracy
	private static final double MAX_MAGNITUDE = Math.PI / 2;
	private static final int NO_TARGET = -1;

	private int newTargetCooldown = 0;

	public EntityHomingArrow(EntityType<EntityHomingArrow> type, Level level) {
		super(type, level);
	}

	public EntityHomingArrow(Level level, LivingEntity shooter, float damage) {
		super(PEEntityTypes.HOMING_ARROW.get(), level);
		setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
		setOwner(shooter);
		this.setBaseDamage(damage);
	}

	@Override
	public void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(DW_TARGET_ID, NO_TARGET); // Target entity id
	}

	@Override
	protected void doPostHurtEffects(@NotNull LivingEntity living) {
		super.doPostHurtEffects(living);
		// Strip damage vulnerability
		living.invulnerableTime = 0;
	}

	@Override
	public void tick() {
		if (tickCount > 3) {
			if (!level().isClientSide) {
				Entity target = getTarget();
				if (target != null && (!target.isAlive() || this.inGround)) {
					entityData.set(DW_TARGET_ID, NO_TARGET);
					target = null;
				}

				if (target == null && !this.inGround && newTargetCooldown <= 0) {
					findNewTarget();
				} else {
					newTargetCooldown--;
				}
			}

			Entity target = getTarget();
			if (target != null && !this.inGround) {
				Vec3 arrowMotion = getDeltaMovement();
				Vec3 particlePos = position().add(arrowMotion.scale(0.25));
				Vec3 particleSpeed = arrowMotion.scale(-0.5).add(0, 0.2, 0);
				this.level().addParticle(ParticleTypes.FLAME, particlePos.x(), particlePos.y(), particlePos.z(), particleSpeed.x(), particleSpeed.y(), particleSpeed.z());
				this.level().addParticle(ParticleTypes.FLAME, particlePos.x(), particlePos.y(), particlePos.z(), particleSpeed.x(), particleSpeed.y(), particleSpeed.z());

				Vec3 targetLoc = target.position().add(0, target.getBbHeight() / 2, 0);

				// Get the vector that points straight from the arrow to the target
				Vec3 lookVec = targetLoc.subtract(position());

				// Create the rotation using the axis and our angle and adjust the vector to it
				Vector3d adjustedLookVec = transform(arrowMotion, lookVec);

				// Tell mc to adjust our rotation accordingly
				shoot(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z, 1.0F, 0);
			}
		}
		super.tick();
	}

	private Vector3d transform(Vec3 arrowMotion, Vec3 lookVec) {
		Vector3d normal = new Vector3d(arrowMotion.x, arrowMotion.y, arrowMotion.z);
		// Find the cross product to determine the axis of rotation
		Vec3 axis = arrowMotion.cross(lookVec).normalize();
		if (axis == Vec3.ZERO) {
			//If the axis is so small that it zero outs, keep the motion as is
			return normal;
		}
		Vector3d look = new Vector3d(lookVec.x, lookVec.y, lookVec.z);
		// Find the angle between the direct vec and arrow vec, and then clamp it, so it arcs a bit
		double angle = Mth.clamp(normal.angle(look), -MAX_MAGNITUDE, MAX_MAGNITUDE);
		return new Matrix3d().rotation(angle, axis.x, axis.y, axis.z).transform(normal);
	}

	private void findNewTarget() {
		List<Mob> candidates = level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(8));

		if (!candidates.isEmpty()) {
			candidates.sort(Comparator.comparingDouble(EntityHomingArrow.this::distanceToSqr));
			entityData.set(DW_TARGET_ID, candidates.get(0).getId());
		}

		newTargetCooldown = 5;
	}

	@Nullable
	private Entity getTarget() {
		return level().getEntity(entityData.get(DW_TARGET_ID));
	}

	@Override
	public boolean ignoreExplosion(@NotNull Explosion explosion) {
		return true;
	}
}