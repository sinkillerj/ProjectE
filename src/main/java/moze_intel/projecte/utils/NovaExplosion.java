package moze_intel.projecte.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NovaExplosion extends Explosion 
{
	private World worldObj;
	
	public NovaExplosion(World world, Entity entity, double x, double y, double z, float radius, boolean isFlaming, boolean isSmoking)
	{
		super(world, entity, x, y, z, radius, isFlaming, isSmoking);
		worldObj = world;
	}
	
	@Override
	public void doExplosionA()
	{
		float f = this.explosionSize;
		HashSet<BlockPos> hashset = Sets.newHashSet();
		int j;
		int k;

		for (int i = 0; i < 16; ++i)
		{
			for (j = 0; j < 16; ++j)
			{
				for (k = 0; k < 16; ++k)
				{
					if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15)
					{
						double d0 = (double)((float)i / 15.0F * 2.0F - 1.0F);
						double d1 = (double)((float)j / 15.0F * 2.0F - 1.0F);
						double d2 = (double)((float)k / 15.0F * 2.0F - 1.0F);
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 /= d3;
						d1 /= d3;
						d2 /= d3;
						float f = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
						double d4 = this.explosionX;
						double d6 = this.explosionY;
						double d8 = this.explosionZ;

						for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F)
						{
							BlockPos blockpos = new BlockPos(d4, d6, d8);
							IBlockState iblockstate = this.worldObj.getBlockState(blockpos);

							if (iblockstate.getBlock().getMaterial() != Material.air)
							{
								float f2 = this.exploder != null ? this.exploder.getExplosionResistance(this, this.worldObj, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(worldObj, blockpos, (Entity)null, this);
								f -= (f2 + 0.3F) * 0.3F;
							}

							if (f > 0.0F && (this.exploder == null || this.exploder.verifyExplosion(this, this.worldObj, blockpos, iblockstate, f)))
							{
								hashset.add(blockpos);
							}

							d4 += d0 * 0.30000001192092896D;
							d6 += d1 * 0.30000001192092896D;
							d8 += d2 * 0.30000001192092896D;
						}
					}
				}
			}
		}

		this.affectedBlockPositions.addAll(hashset);
		this.explosionSize = f;
	}
	
	@Override
	public void doExplosionB(boolean spawnParticles)
	{
		this.worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		if (this.explosionSize >= 2.0F && this.isSmoking)
		{
			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
		}
		else
		{
			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D, new int[0]);
		}

		Iterator iterator;
		BlockPos blockpos;
		List<ItemStack> allDrops = Lists.newArrayList();

		if (this.isSmoking)
		{
			iterator = this.affectedBlockPositions.iterator();

			while (iterator.hasNext())
			{
				blockpos = (BlockPos)iterator.next();
				Block block = this.worldObj.getBlockState(blockpos).getBlock();

				if (spawnParticles)
				{
					double d0 = (double)((float)blockpos.getX() + this.worldObj.rand.nextFloat());
					double d1 = (double)((float)blockpos.getY() + this.worldObj.rand.nextFloat());
					double d2 = (double)((float)blockpos.getZ() + this.worldObj.rand.nextFloat());
					double d3 = d0 - this.explosionX;
					double d4 = d1 - this.explosionY;
					double d5 = d2 - this.explosionZ;
					double d6 = (double)MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
					d3 /= d6;
					d4 /= d6;
					d5 /= d6;
					double d7 = 0.5D / (d6 / (double)this.explosionSize + 0.1D);
					d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
					d3 *= d7;
					d4 *= d7;
					d5 *= d7;
					this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D, (d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5, new int[0]);
					this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
				}

				if (block.getMaterial() != Material.air)
				{
					List<ItemStack> drops = block.getDrops(worldObj, blockpos, this.worldObj.getBlockState(blockpos), 0);
					if (drops.size() > 0)
					{
						allDrops.addAll(drops);
					}

					block.onBlockExploded(worldObj, blockpos, this);
				}
			}
		WorldHelper.createLootDrop(drops, world, explosionX, explosionY, explosionZ);
		}
	}
}
