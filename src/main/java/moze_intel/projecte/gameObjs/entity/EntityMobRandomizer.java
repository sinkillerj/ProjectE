package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.EntityRandomizerHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
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
	public void handleEntityEvent(byte id) {
		if (id == EntityEvent.DEATH) {
			for (int i = 0; i < 8; i++) {
				level().addParticle(ParticleTypes.PORTAL, getX(), getY() + random.nextDouble() * 2.0D, getZ(), random.nextGaussian(), 0.0D, random.nextGaussian());
			}
		}
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult result) {
		super.onHitEntity(result);
		if (!level().isClientSide() && result.getEntity() instanceof Mob ent && getOwner() instanceof Player player) {
			ServerLevel level = (ServerLevel) level();
			Mob randomized = EntityRandomizerHelper.getRandomEntity(level, ent);
			//TODO: Ideally we wouldn't consume fuel until after we make sure it was able to be added to the world and we remove it
			// but odds are we will be able to so for now I am not going to worry about it
			if (randomized != null && EMCHelper.consumePlayerFuel(player, 384) != -1) {
				randomized.snapTo(ent.position(), ent.getYRot(), ent.getXRot());
				EventHooks.finalizeMobSpawn(randomized, level, level.getCurrentDifficultyAt(randomized.blockPosition()), EntitySpawnReason.CONVERSION, null);
				level.tryAddFreshEntityWithPassengers(randomized);
				if (randomized.isAddedToLevel()) {
					randomized.spawnAnim();
					//Don't remove the old entity until the new one is added in case another mod is cancelling the spawning
					ent.discard();
				}
			}
		}
	}
}
