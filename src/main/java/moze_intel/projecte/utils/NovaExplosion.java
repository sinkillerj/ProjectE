package moze_intel.projecte.utils;

import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class NovaExplosion extends Explosion {

	// Copies of private super fields
	private final Level level;
	private final Explosion.BlockInteraction mode;
	private final double x, y, z;
	private final float size;

	public NovaExplosion(Level level, @Nullable Entity entity, double x, double y, double z, float radius, boolean causesFire, Explosion.BlockInteraction mode) {
		super(level, entity, null, null, x, y, z, radius, causesFire, mode);
		this.level = level;
		this.mode = mode;
		this.size = radius;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// [VanillaCopy] super, but collecting all drops into one place, and no fire
	@Override
	public void finalizeExplosion(boolean spawnParticles) {
		if (level.isClientSide) {
			level.playLocalSound(x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F, false);
		}
		boolean hasExplosionMode = mode != Explosion.BlockInteraction.NONE;
		if (spawnParticles) {
			if (hasExplosionMode && size >= 2.0F) {
				level.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1.0D, 0.0D, 0.0D);
			} else {
				level.addParticle(ParticleTypes.EXPLOSION, x, y, z, 1.0D, 0.0D, 0.0D);
			}
		}
		if (hasExplosionMode) {
			NonNullList<ItemStack> allDrops = NonNullList.create();
			List<BlockPos> affectedBlockPositions = getToBlow();
			Collections.shuffle(affectedBlockPositions, level.random);
			for (BlockPos pos : affectedBlockPositions) {
				BlockState state = level.getBlockState(pos);
				if (!state.isAir()) {
					if (spawnParticles) {
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
						double d7 = 0.5D / (diff / (double) size + 0.1D);
						d7 = d7 * (double) (level.random.nextFloat() * level.random.nextFloat() + 0.3F);
						diffX = diffX * d7;
						diffY = diffY * d7;
						diffZ = diffZ * d7;
						level.addParticle(ParticleTypes.POOF, (adjustedX + x) / 2.0D, (adjustedY + y) / 2.0D, (adjustedZ + z) / 2.0D, diffX, diffY, diffZ);
						level.addParticle(ParticleTypes.SMOKE, adjustedX, adjustedY, adjustedZ, diffX, diffY, diffZ);
					}
					//Ensure we are immutable so that changing blocks doesn't act weird
					pos = pos.immutable();
					level.getProfiler().push("explosion_blocks");
					if (level instanceof ServerLevel serverLevel && state.canDropFromExplosion(level, pos, this)) {
						BlockEntity blockEntity = state.hasBlockEntity() ? WorldHelper.getBlockEntity(serverLevel, pos) : null;
						LootContext.Builder builder = new LootContext.Builder(serverLevel)
								.withRandom(serverLevel.random)
								.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
								.withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
								.withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
								.withOptionalParameter(LootContextParams.THIS_ENTITY, getExploder());
						if (mode == Explosion.BlockInteraction.DESTROY) {
							builder.withParameter(LootContextParams.EXPLOSION_RADIUS, size);
						}

						// PE: Collect the drops we can, spawn the stuff we can't
						allDrops.addAll(state.getDrops(builder));
					}
					state.onBlockExploded(level, pos, this);
					level.getProfiler().pop();
				}
			}

			// PE: Drop all together
			LivingEntity placer = getSourceMob();
			if (placer == null) {
				WorldHelper.createLootDrop(allDrops, level, x, y, z);
			} else {
				WorldHelper.createLootDrop(allDrops, level, placer.blockPosition());
			}
		}
	}
}