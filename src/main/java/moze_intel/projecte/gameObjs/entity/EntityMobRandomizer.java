package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.EntityRandomizerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Rabbit.RabbitGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.network.protocol.Packet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.network.NetworkHooks;

public class EntityMobRandomizer extends ThrowableProjectile {

	public EntityMobRandomizer(EntityType<EntityMobRandomizer> type, Level world) {
		super(type, world);
	}

	public EntityMobRandomizer(Player entity, Level world) {
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
				this.discard();
			}
		}
	}

	@Override
	public float getGravity() {
		return 0;
	}

	@Override
	protected void onHit(@Nonnull HitResult mop) {
		if (getCommandSenderWorld().isClientSide) {
			for (int i = 0; i < 4; ++i) {
				getCommandSenderWorld().addParticle(ParticleTypes.PORTAL, getX(), getY() + random.nextDouble() * 2.0D, getZ(), random.nextGaussian(), 0.0D, random.nextGaussian());
			}
			return;
		}
		if (isInWater() || !(mop instanceof EntityHitResult) || !(((EntityHitResult) mop).getEntity() instanceof Mob)) {
			discard();
			return;
		}
		Entity thrower = getOwner();
		if (!(thrower instanceof Player)) {
			discard();
			return;
		}

		Mob ent = (Mob) ((EntityHitResult) mop).getEntity();
		Mob randomized = EntityRandomizerHelper.getRandomEntity(this.getCommandSenderWorld(), ent);
		if (randomized != null && EMCHelper.consumePlayerFuel((Player) thrower, 384) != -1) {
			ent.discard();
			randomized.moveTo(ent.getX(), ent.getY(), ent.getZ(), ent.getYRot(), ent.getXRot());
			SpawnGroupData data;
			if (randomized instanceof Rabbit && ((Rabbit) randomized).getRabbitType() == 99) {
				//If we are creating a rabbit and it is supposed to be the killer bunny, we need to pass that data
				// to onInitialSpawn or it will reset it to a random type of rabbit
				data = new RabbitGroupData(99);
			} else {
				data = null;
			}
			randomized.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(randomized.blockPosition()), MobSpawnType.CONVERSION, data, null);
			getCommandSenderWorld().addFreshEntity(randomized);
			randomized.spawnAnim();
		}
		discard();
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