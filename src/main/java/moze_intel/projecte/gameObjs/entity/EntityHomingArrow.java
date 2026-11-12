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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class EntityHomingArrow extends ThrowableProjectile {

	private static final EntityDataAccessor<Integer> DW_TARGET_ID = SynchedEntityData.defineId(EntityHomingArrow.class, EntityDataSerializers.INT);
	private static final double MAX_MAGNITUDE = Math.PI / 2;
	private static final int NO_TARGET = -1;

	private int newTargetCooldown = 0;
	private float damage = 2.0F;

	public EntityHomingArrow(EntityType<? extends EntityHomingArrow> type, Level level) {
		super(type, level);
	}

	public EntityHomingArrow(Level level, LivingEntity shooter, float damage) {
		super(PEEntityTypes.HOMING_ARROW.get(), level);
		setOwner(shooter);
		setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
		this.damage = damage;
	}

	@Override
	protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
		builder.define(DW_TARGET_ID, NO_TARGET);
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult result) {
		super.onHitEntity(result);
		if (!level().isClientSide() && result.getEntity() instanceof LivingEntity living) {
			living.setInvulnerableTime(0);
			Entity owner = getOwner();
			living.hurt(damageSources().mobProjectile(this, owner instanceof LivingEntity livingOwner ? livingOwner : null), damage);
		}
	}

	@Override
	protected void onHit(@NotNull HitResult result) {
		super.onHit(result);
		if (!level().isClientSide()) {
			discard();
		}
	}

	@Override
	public void tick() {
		if (tickCount > 3) {
			boolean stuck = onGround();
			if (!level().isClientSide()) {
				Entity target = getTarget();
				if (target != null && (!target.isAlive() || stuck)) {
					entityData.set(DW_TARGET_ID, NO_TARGET);
					target = null;
				}

				if (target == null && !stuck && newTargetCooldown <= 0) {
					findNewTarget();
				} else {
					newTargetCooldown--;
				}
			}

			Entity target = getTarget();
			if (target != null && !stuck) {
				Vec3 arrowMotion = getDeltaMovement();
				Vec3 particlePos = position().add(arrowMotion.scale(0.25));
				Vec3 particleSpeed = arrowMotion.scale(-0.5).add(0, 0.2, 0);
				level().addParticle(ParticleTypes.FLAME, particlePos.x(), particlePos.y(), particlePos.z(), particleSpeed.x(), particleSpeed.y(), particleSpeed.z());
				level().addParticle(ParticleTypes.FLAME, particlePos.x(), particlePos.y(), particlePos.z(), particleSpeed.x(), particleSpeed.y(), particleSpeed.z());

				Vec3 targetLoc = target.position().add(0, target.getBbHeight() / 2, 0);
				Vec3 lookVec = targetLoc.subtract(position());
				Vector3d adjustedLookVec = transform(arrowMotion, lookVec);
				setDeltaMovement(adjustedLookVec.x, adjustedLookVec.y, adjustedLookVec.z);
			}
		}
		super.tick();
	}

	private Vector3d transform(Vec3 arrowMotion, Vec3 lookVec) {
		Vector3d normal = new Vector3d(arrowMotion.x, arrowMotion.y, arrowMotion.z);
		Vec3 axis = arrowMotion.cross(lookVec).normalize();
		if (axis == Vec3.ZERO) {
			return normal;
		}
		Vector3d look = new Vector3d(lookVec.x, lookVec.y, lookVec.z);
		double angle = Mth.clamp(normal.angle(look), -MAX_MAGNITUDE, MAX_MAGNITUDE);
		return new Matrix3d().rotation(angle, axis.x, axis.y, axis.z).transform(normal);
	}

	private void findNewTarget() {
		List<Mob> candidates = level().getEntitiesOfClass(Mob.class, getBoundingBox().inflate(8));

		if (!candidates.isEmpty()) {
			candidates.sort(Comparator.comparingDouble(this::distanceToSqr));
			entityData.set(DW_TARGET_ID, candidates.getFirst().getId());
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
