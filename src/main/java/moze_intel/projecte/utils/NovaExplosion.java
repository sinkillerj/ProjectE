package moze_intel.projecte.utils;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class NovaExplosion extends Explosion {

	// Copies of private super fields
	private final World world;
	private final Explosion.Mode mode;
	private final double x, y, z;
	private final float size;

	public NovaExplosion(World world, @Nullable Entity entity, double x, double y, double z, float radius, boolean causesFire, Explosion.Mode mode) {
		super(world, entity, null, null, x, y, z, radius, causesFire, mode);
		this.world = world;
		this.mode = mode;
		this.size = radius;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// [VanillaCopy] super, but collecting all drops into one place, and no fire
	@Override
	public void doExplosionB(boolean spawnParticles) {
		if (world.isRemote) {
			world.playSound(x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F, false);
		}
		boolean hasExplosionMode = mode != Explosion.Mode.NONE;
		if (spawnParticles) {
			if (hasExplosionMode && size >= 2.0F) {
				world.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1.0D, 0.0D, 0.0D);
			} else {
				world.addParticle(ParticleTypes.EXPLOSION, x, y, z, 1.0D, 0.0D, 0.0D);
			}
		}
		if (hasExplosionMode) {
			NonNullList<ItemStack> allDrops = NonNullList.create();
			List<BlockPos> affectedBlockPositions = getAffectedBlockPositions();
			Collections.shuffle(affectedBlockPositions, world.rand);
			for (BlockPos pos : affectedBlockPositions) {
				BlockState state = world.getBlockState(pos);
				if (!state.isAir(world, pos)) {
					if (spawnParticles) {
						double adjustedX = pos.getX() + world.rand.nextFloat();
						double adjustedY = pos.getY() + world.rand.nextFloat();
						double adjustedZ = pos.getZ() + world.rand.nextFloat();
						double diffX = adjustedX - x;
						double diffY = adjustedY - y;
						double diffZ = adjustedZ - z;
						double diff = MathHelper.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
						diffX = diffX / diff;
						diffY = diffY / diff;
						diffZ = diffZ / diff;
						double d7 = 0.5D / (diff / (double) size + 0.1D);
						d7 = d7 * (double) (world.rand.nextFloat() * world.rand.nextFloat() + 0.3F);
						diffX = diffX * d7;
						diffY = diffY * d7;
						diffZ = diffZ * d7;
						world.addParticle(ParticleTypes.POOF, (adjustedX + x) / 2.0D, (adjustedY + y) / 2.0D, (adjustedZ + z) / 2.0D, diffX, diffY, diffZ);
						world.addParticle(ParticleTypes.SMOKE, adjustedX, adjustedY, adjustedZ, diffX, diffY, diffZ);
					}
					//Ensure we are immutable so that changing blocks doesn't act weird
					pos = pos.toImmutable();
					world.getProfiler().startSection("explosion_blocks");
					if (world instanceof ServerWorld && state.canDropFromExplosion(world, pos, this)) {
						TileEntity tileentity = state.hasTileEntity() ? WorldHelper.getTileEntity(world, pos) : null;
						LootContext.Builder builder = new LootContext.Builder((ServerWorld) world)
								.withRandom(world.rand)
								.withParameter(LootParameters.field_237457_g_, Vector3d.copyCentered(pos))
								.withParameter(LootParameters.TOOL, ItemStack.EMPTY)
								.withNullableParameter(LootParameters.BLOCK_ENTITY, tileentity)
								.withNullableParameter(LootParameters.THIS_ENTITY, getExploder());
						if (mode == Explosion.Mode.DESTROY) {
							builder.withParameter(LootParameters.EXPLOSION_RADIUS, size);
						}

						// PE: Collect the drops we can, spawn the stuff we can't
						allDrops.addAll(state.getDrops(builder));
					}
					state.onBlockExploded(world, pos, this);
					world.getProfiler().endSection();
				}
			}

			// PE: Drop all together
			LivingEntity placer = getExplosivePlacedBy();
			if (placer == null) {
				WorldHelper.createLootDrop(allDrops, world, x, y, z);
			} else {
				WorldHelper.createLootDrop(allDrops, world, placer.getPosition());
			}
		}
	}
}