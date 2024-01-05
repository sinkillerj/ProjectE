package moze_intel.projecte.utils;

import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class NovaExplosion extends Explosion {

	// Copies of private super fields
	private final Level level;

	public NovaExplosion(Level level, @Nullable Entity entity, double x, double y, double z, float radius, Explosion.BlockInteraction mode) {
		//Nova Explosions don't cause fire
		super(level, entity, x, y, z, radius, false, mode);
		this.level = level;
	}

	// [VanillaCopy] super, but collecting all drops into one place, and no fire (so we don't have to copy that bit)
	@Override
	public void finalizeExplosion(boolean spawnParticles) {
		Vec3 center = center();
		double x = center.x;
		double y = center.y;
		double z = center.z;
		if (level.isClientSide) {
			level.playLocalSound(x, y, z, getExplosionSound(), SoundSource.BLOCKS, 4.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F, false);
		}
		boolean interactsWithBlocks = interactsWithBlocks();
		if (spawnParticles) {
			ParticleOptions particleOptions;
			if (interactsWithBlocks && radius() >= 2.0F) {
				particleOptions = getLargeExplosionParticles();
			} else {
				particleOptions = getSmallExplosionParticles();
			}
			level.addParticle(particleOptions, x, y, z, 1.0D, 0.0D, 0.0D);
		}
		List<BlockPos> toBlow = getToBlow();
		if (interactsWithBlocks) {
			this.level.getProfiler().push("explosion_blocks");

			NonNullList<ItemStack> allDrops = NonNullList.create();
			Util.shuffle(toBlow, this.level.random);
			for (BlockPos pos : toBlow) {
				//Ensure we are immutable so that changing blocks doesn't act weird
				pos = pos.immutable();
				BlockState state = level.getBlockState(pos);
				// PE: Collect the drops we can, spawn the stuff we can't
				state.onExplosionHit(this.level, pos, this, (stack, position) -> allDrops.add(stack));
				//TODO - 1.20.4: Do we still want this to have particle spawning happen here?
				if (spawnParticles && !state.isAir()) {
					double adjustedX = pos.getX() + level.random.nextFloat();
					double adjustedY = pos.getY() + level.random.nextFloat();
					double adjustedZ = pos.getZ() + level.random.nextFloat();
					double diffX = adjustedX - x;
					double diffY = adjustedY - y;
					double diffZ = adjustedZ - z;
					double diff = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
					diffX = diffX / diff;
					diffY = diffY / diff;
					diffZ = diffZ / diff;
					double d7 = 0.5D / (diff / (double) radius() + 0.1D);
					d7 = d7 * (double) (level.random.nextFloat() * level.random.nextFloat() + 0.3F);
					diffX = diffX * d7;
					diffY = diffY * d7;
					diffZ = diffZ * d7;
					level.addParticle(ParticleTypes.POOF, (adjustedX + x) / 2.0D, (adjustedY + y) / 2.0D, (adjustedZ + z) / 2.0D, diffX, diffY, diffZ);
					level.addParticle(ParticleTypes.SMOKE, adjustedX, adjustedY, adjustedZ, diffX, diffY, diffZ);
				}
			}

			// PE: Drop all together
			LivingEntity placer = getIndirectSourceEntity();
			if (placer == null) {
				WorldHelper.createLootDrop(allDrops, level, x, y, z);
			} else {
				WorldHelper.createLootDrop(allDrops, level, placer.blockPosition());
			}

			this.level.getProfiler().pop();
		}
	}
}