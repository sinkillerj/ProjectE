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
	protected void registerData() {
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.getEntityWorld().isRemote) {
			if (ticksExisted > 400 || isInWater() || !getEntityWorld().isBlockPresent(getPosition())) {
				this.remove();
			}
		}
	}

	@Override
	public float getGravityVelocity() {
		return 0;
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult mop) {
		if (getEntityWorld().isRemote) {
			for (int i = 0; i < 4; ++i) {
				getEntityWorld().addParticle(ParticleTypes.PORTAL, getPosX(), getPosY() + rand.nextDouble() * 2.0D, getPosZ(), rand.nextGaussian(), 0.0D, rand.nextGaussian());
			}
			return;
		}
		if (isInWater() || !(mop instanceof EntityRayTraceResult) || !(((EntityRayTraceResult) mop).getEntity() instanceof MobEntity)) {
			remove();
			return;
		}
		Entity thrower = func_234616_v_();
		if (!(thrower instanceof PlayerEntity)) {
			remove();
			return;
		}

		MobEntity ent = (MobEntity) ((EntityRayTraceResult) mop).getEntity();
		MobEntity randomized = EntityRandomizerHelper.getRandomEntity(this.getEntityWorld(), ent);
		if (randomized != null && EMCHelper.consumePlayerFuel((PlayerEntity) thrower, 384) != -1) {
			ent.remove();
			randomized.setLocationAndAngles(ent.getPosX(), ent.getPosY(), ent.getPosZ(), ent.rotationYaw, ent.rotationPitch);
			ILivingEntityData data;
			if (randomized instanceof RabbitEntity && ((RabbitEntity) randomized).getRabbitType() == 99) {
				//If we are creating a rabbit and it is supposed to be the killer bunny, we need to pass that data
				// to onInitialSpawn or it will reset it to a random type of rabbit
				data = new RabbitData(99);
			} else {
				data = null;
			}
			randomized.onInitialSpawn((ServerWorld) world, world.getDifficultyForLocation(randomized.getPosition()), SpawnReason.CONVERSION, data, null);
			getEntityWorld().addEntity(randomized);
			randomized.spawnExplosionParticle();
		}
		remove();
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}
}