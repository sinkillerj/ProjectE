package moze_intel.projecte.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

import javax.annotation.Nullable;

public class NovaExplosion extends Explosion 
{
	// Copies of private super fields
	private final World world;
	private final Explosion.Mode mode;
	private final double x, y, z;
	private final float size;

	public NovaExplosion(World world, @Nullable Entity entity, double x, double y, double z, float radius, boolean causesFire, Explosion.Mode mode)
	{
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
	public void doExplosionB(boolean spawnParticles)
	{
		this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);
		boolean flag = this.mode != Explosion.Mode.NONE;
		if (!(this.size < 2.0F) && flag) {
			this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
		} else {
			this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
		}

		NonNullList<ItemStack> allDrops = NonNullList.create();
		if (flag) {
			for(BlockPos blockpos : this.getAffectedBlockPositions()) {
				BlockState blockstate = this.world.getBlockState(blockpos);
				Block block = blockstate.getBlock();
				if (spawnParticles) {
					double d0 = (double)((float)blockpos.getX() + this.world.rand.nextFloat());
					double d1 = (double)((float)blockpos.getY() + this.world.rand.nextFloat());
					double d2 = (double)((float)blockpos.getZ() + this.world.rand.nextFloat());
					double d3 = d0 - this.x;
					double d4 = d1 - this.y;
					double d5 = d2 - this.z;
					double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
					d3 = d3 / d6;
					d4 = d4 / d6;
					d5 = d5 / d6;
					double d7 = 0.5D / (d6 / (double)this.size + 0.1D);
					d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
					d3 = d3 * d7;
					d4 = d4 * d7;
					d5 = d5 * d7;
					this.world.addParticle(ParticleTypes.POOF, (d0 + this.x) / 2.0D, (d1 + this.y) / 2.0D, (d2 + this.z) / 2.0D, d3, d4, d5);
					this.world.addParticle(ParticleTypes.SMOKE, d0, d1, d2, d3, d4, d5);
				}

				if (!blockstate.isAir(this.world, blockpos)) {
					if (this.world instanceof ServerWorld && blockstate.canDropFromExplosion(this.world, blockpos, this)) {
						TileEntity tileentity = blockstate.hasTileEntity() ? this.world.getTileEntity(blockpos) : null;
						LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.world)).withRandom(this.world.rand).withParameter(LootParameters.POSITION, blockpos).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, tileentity);
						if (this.mode == Explosion.Mode.DESTROY) {
							lootcontext$builder.withParameter(LootParameters.EXPLOSION_RADIUS, this.size);
						}

						// PE: Collect the drops we can, spawn the stuff we can't
						allDrops.addAll(blockstate.getDrops( lootcontext$builder));
						blockstate.spawnAdditionalDrops(world, blockpos, ItemStack.EMPTY);
					}

					this.world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 3);
					block.onExplosionDestroy(this.world, blockpos, this);
				}
			}
		}

		// PE: Drop all together
		if (getExplosivePlacedBy() != null)
        {
            WorldHelper.createLootDrop(allDrops, this.world, new BlockPos(getExplosivePlacedBy()));
        }
        else
        {
            WorldHelper.createLootDrop(allDrops, this.world, x, y, z);
        }
	}
}
