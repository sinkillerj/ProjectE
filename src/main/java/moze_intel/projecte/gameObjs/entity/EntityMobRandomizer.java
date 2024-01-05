package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.EntityRandomizerHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Rabbit.RabbitGroupData;
import net.minecraft.world.entity.animal.Rabbit.Variant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

public class EntityMobRandomizer extends NoGravityThrowableProjectile {

	public EntityMobRandomizer(EntityType<EntityMobRandomizer> type, Level level) {
		super(type, level);
	}

	public EntityMobRandomizer(Player entity, Level level) {
		super(PEEntityTypes.MOB_RANDOMIZER.get(), entity, level);
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide && isAlive() && isInWater()) {
			this.discard();
		}
	}

	@Override
	protected void onHit(@NotNull HitResult result) {
		if (level().isClientSide) {
			for (int i = 0; i < 4; ++i) {
				level().addParticle(ParticleTypes.PORTAL, getX(), getY() + random.nextDouble() * 2.0D, getZ(), random.nextGaussian(), 0.0D, random.nextGaussian());
			}
		}
		if (!isInWater()) {
			super.onHit(result);
		}
		discard();
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult result) {
		super.onHitEntity(result);
		if (!level().isClientSide && result.getEntity() instanceof Mob ent && getOwner() instanceof Player player) {
			ServerLevel level = (ServerLevel) level();
			Mob randomized = EntityRandomizerHelper.getRandomEntity(level, ent);
			//TODO: Ideally we wouldn't consume fuel until after we make sure it was able to be added to the world and we remove it
			// but odds are we will be able to so for now I am not going to worry about it
			if (randomized != null && EMCHelper.consumePlayerFuel(player, 384) != -1) {
				randomized.moveTo(ent.getX(), ent.getY(), ent.getZ(), ent.getYRot(), ent.getXRot());
				SpawnGroupData data;
				if (randomized instanceof Rabbit rabbit && rabbit.getVariant() == Variant.EVIL) {
					//If we are creating a rabbit, and it is supposed to be the killer bunny, we need to pass that data
					// to onInitialSpawn, or it will reset it to a random type of rabbit
					data = new RabbitGroupData(Variant.EVIL);
				} else {
					data = null;
				}
				EventHooks.onFinalizeSpawn(randomized, level, level.getCurrentDifficultyAt(randomized.blockPosition()), MobSpawnType.CONVERSION, data, null);
				level.tryAddFreshEntityWithPassengers(randomized);
				if (randomized.isAddedToWorld()) {
					randomized.spawnAnim();
					//Don't remove the old entity until the new one is added in case another mod is cancelling the spawning
					ent.discard();
				}
			}
		}
	}

	@Override
	public boolean ignoreExplosion(@NotNull Explosion explosion) {
		return true;
	}
}