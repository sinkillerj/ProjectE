package moze_intel.projecte.utils;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class NovaExplosion extends Explosion {

	// Copies of private super fields
	private final World world;
	private final Explosion.Mode mode;
	private final double x, y, z;
	private final float size;

	public NovaExplosion(World world, @Nullable Entity entity, double x, double y, double z, float radius, boolean causesFire, Explosion.Mode mode) {
		super(world, entity, x, y, z, radius, causesFire, mode);
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
		world.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
		boolean differentMode = mode != Explosion.Mode.NONE;
		if (size >= 2.0F && differentMode) {
			world.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1.0D, 0.0D, 0.0D);
		} else {
			world.addParticle(ParticleTypes.EXPLOSION, x, y, z, 1.0D, 0.0D, 0.0D);
		}

		NonNullList<ItemStack> allDrops = NonNullList.create();
		if (differentMode) {
			for (BlockPos pos : getAffectedBlockPositions()) {
				BlockState state = world.getBlockState(pos);
				Block block = state.getBlock();
				if (spawnParticles) {
					double d0 = (float) pos.getX() + world.rand.nextFloat();
					double d1 = (float) pos.getY() + world.rand.nextFloat();
					double d2 = (float) pos.getZ() + world.rand.nextFloat();
					double d3 = d0 - x;
					double d4 = d1 - y;
					double d5 = d2 - z;
					double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
					d3 = d3 / d6;
					d4 = d4 / d6;
					d5 = d5 / d6;
					double d7 = 0.5D / (d6 / (double) size + 0.1D);
					d7 = d7 * (double) (world.rand.nextFloat() * world.rand.nextFloat() + 0.3F);
					d3 = d3 * d7;
					d4 = d4 * d7;
					d5 = d5 * d7;
					world.addParticle(ParticleTypes.POOF, (d0 + x) / 2.0D, (d1 + y) / 2.0D, (d2 + z) / 2.0D, d3, d4, d5);
					world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, d3, d4, d5);
				}

				if (!world.isAirBlock(pos)) {
					//Ensure we are immutable so that changing blocks doesn't act weird
					pos = pos.toImmutable();
					if (world instanceof ServerWorld && state.canDropFromExplosion(world, pos, this)) {
						TileEntity tileentity = state.hasTileEntity() ? world.getTileEntity(pos) : null;
						LootContext.Builder builder = new LootContext.Builder((ServerWorld) world).withRandom(world.rand).withParameter(LootParameters.POSITION, pos)
								.withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, tileentity);
						if (mode == Explosion.Mode.DESTROY) {
							builder.withParameter(LootParameters.EXPLOSION_RADIUS, size);
						}

						// PE: Collect the drops we can, spawn the stuff we can't
						allDrops.addAll(state.getDrops(builder));
						state.spawnAdditionalDrops(world, pos, ItemStack.EMPTY);
					}
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
					block.onExplosionDestroy(world, pos, this);
				}
			}
		}

		// PE: Drop all together
		if (getExplosivePlacedBy() != null) {
			WorldHelper.createLootDrop(allDrops, world, new BlockPos(getExplosivePlacedBy()));
		} else {
			WorldHelper.createLootDrop(allDrops, world, x, y, z);
		}
	}
}