package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.EntityRandomizerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.RabbitEntity.RabbitData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityMobRandomizer extends ThrowableEntity {

	public EntityMobRandomizer(EntityType<EntityMobRandomizer> type, World world) {
		super(type, world);
	}

	public EntityMobRandomizer(PlayerEntity entity, World world) {
		super(PEEntityTypes.MOB_RANDOMIZER.get(), entity, world);
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.getCommandSenderWorld().isClientSide) {
			if (tickCount > 400 || isInWater() || !getCommandSenderWorld().isLoaded(blockPosition())) {
				this.remove();
			}
		}
	}

	@Override
	public float getGravity() {
		return 0;
	}

	@Override
	protected void onHit(@Nonnull RayTraceResult mop) {
		if (getCommandSenderWorld().isClientSide) {
			for (int i = 0; i < 4; ++i) {
				getCommandSenderWorld().addParticle(ParticleTypes.PORTAL, getX(), getY() + random.nextDouble() * 2.0D, getZ(), random.nextGaussian(), 0.0D, random.nextGaussian());
			}
			return;
		}
		if (isInWater() || !(mop instanceof EntityRayTraceResult) || !(((EntityRayTraceResult) mop).getEntity() instanceof MobEntity)) {
			remove();
			return;
		}
		Entity thrower = getOwner();
		if (!(thrower instanceof PlayerEntity)) {
			remove();
			return;
		}

		MobEntity ent = (MobEntity) ((EntityRayTraceResult) mop).getEntity();
		MobEntity randomized = EntityRandomizerHelper.getRandomEntity(this.getCommandSenderWorld(), ent);
		if (randomized != null && EMCHelper.consumePlayerFuel((PlayerEntity) thrower, 384) != -1) {
			ent.remove();
			randomized.moveTo(ent.getX(), ent.getY(), ent.getZ(), ent.yRot, ent.xRot);
			ILivingEntityData data;
			if (randomized instanceof RabbitEntity && ((RabbitEntity) randomized).getRabbitType() == 99) {
				//If we are creating a rabbit and it is supposed to be the killer bunny, we need to pass that data
				// to onInitialSpawn or it will reset it to a random type of rabbit
				data = new RabbitData(99);
			} else {
				data = null;
			}
			randomized.finalizeSpawn((ServerWorld) level, level.getCurrentDifficultyAt(randomized.blockPosition()), SpawnReason.CONVERSION, data, null);
			getCommandSenderWorld().addFreshEntity(randomized);
			randomized.spawnAnim();
		}
		remove();
	}

	@Nonnull
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}
}