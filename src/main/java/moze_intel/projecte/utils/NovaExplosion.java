package moze_intel.projecte.utils;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class NovaExplosion extends Explosion 
{
	// Copies of private super fields
	private final World world;
	private final boolean damagesTerrain;
	private final double x, y, z;
	private final float size;
	
	public NovaExplosion(World world, Entity entity, double x, double y, double z, float radius, boolean causesFire, boolean damagesTerrain)
	{
		super(world, entity, x, y, z, radius, causesFire, damagesTerrain);
		this.world = world;
		this.damagesTerrain = damagesTerrain;
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
		if (!(this.size < 2.0F) && this.damagesTerrain) {
			this.world.spawnParticle(Particles.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
		} else {
			this.world.spawnParticle(Particles.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
		}

		NonNullList<ItemStack> allDrops = NonNullList.create();

		if (this.damagesTerrain) {
			for(BlockPos blockpos : this.getAffectedBlockPositions()) {
				IBlockState iblockstate = this.world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();
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
					this.world.spawnParticle(Particles.POOF, (d0 + this.x) / 2.0D, (d1 + this.y) / 2.0D, (d2 + this.z) / 2.0D, d3, d4, d5);
					this.world.spawnParticle(Particles.SMOKE, d0, d1, d2, d3, d4, d5);
				}

				if (!iblockstate.isAir(world, blockpos)) {
					if (block.canDropFromExplosion(this)) {
						// PE: Collect all drops in one place
						NonNullList<ItemStack> drops = NonNullList.create();
						iblockstate.getDrops(drops, world, blockpos, 0);
						allDrops.addAll(drops);
					}

					iblockstate.onBlockExploded(this.world, blockpos, this);
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
