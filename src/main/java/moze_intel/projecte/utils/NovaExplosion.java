package moze_intel.projecte.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class NovaExplosion extends Explosion 
{
	private final World worldObj;
	
	public NovaExplosion(World world, Entity entity, double x, double y, double z, float radius, boolean isFlaming, boolean isSmoking)
	{
		super(world, entity, x, y, z, radius, isFlaming, isSmoking);
		worldObj = world;
	}
	
	@Override
	public void doExplosionA()
	{
		float initialSize = this.size;

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
						float f = initialSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
						double d4 = this.getPosition().x;
						double d6 = this.getPosition().y;
						double d8 = this.getPosition().z;

						for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F)
						{
							BlockPos blockpos = new BlockPos(d4, d6, d8);
							IBlockState iblockstate = this.worldObj.getBlockState(blockpos);

							if (iblockstate.getMaterial() != Material.AIR)
							{
								float f2 = this.getExplosivePlacedBy() != null ? this.getExplosivePlacedBy().getExplosionResistance(this, this.worldObj, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(worldObj, blockpos, null, this);
								f -= (f2 + 0.3F) * 0.3F;
							}

							if (f > 0.0F && (this.getExplosivePlacedBy() == null || this.getExplosivePlacedBy().canExplosionDestroyBlock(this, this.worldObj, blockpos, iblockstate, f)))
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

		this.getAffectedBlockPositions().addAll(hashset);
		this.size = initialSize;
		net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.worldObj, this, Collections.emptyList(), this.size);
	}
	
	@Override
	public void doExplosionB(boolean spawnParticles)
	{
		float cachedExplosionSize = this.size;
		double x = getPosition().x;
		double y = getPosition().y;
		double z = getPosition().z;

		this.worldObj.playSound(null, x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		if (cachedExplosionSize >= 2.0F)
		{
			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0D, 0.0D, 0.0D);
		}
		else
		{
			this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, x, y, z, 1.0D, 0.0D, 0.0D);
		}

		Iterator<BlockPos> iterator;
		BlockPos blockpos;
		List<ItemStack> allDrops = new ArrayList<>();

		iterator = getAffectedBlockPositions().iterator();

		while (iterator.hasNext())
        {
            blockpos = iterator.next();
			IBlockState state = worldObj.getBlockState(blockpos);
            Block block = state.getBlock();

            if (spawnParticles)
            {
                double d0 = (double)((float)blockpos.getX() + this.worldObj.rand.nextFloat());
                double d1 = (double)((float)blockpos.getY() + this.worldObj.rand.nextFloat());
                double d2 = (double)((float)blockpos.getZ() + this.worldObj.rand.nextFloat());
                double d3 = d0 - x;
                double d4 = d1 - y;
                double d5 = d2 - z;
                double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                d3 /= d6;
                d4 /= d6;
                d5 /= d6;
                double d7 = 0.5D / (d6 / (double)cachedExplosionSize + 0.1D);
                d7 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
                d3 *= d7;
                d4 *= d7;
                d5 *= d7;
                this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + x * 1.0D) / 2.0D, (d1 + y * 1.0D) / 2.0D, (d2 + z * 1.0D) / 2.0D, d3, d4, d5);
                this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
            }

            if (state.getMaterial() != Material.AIR)
            {
                List<ItemStack> drops = block.getDrops(worldObj, blockpos, this.worldObj.getBlockState(blockpos), 0);
                if (drops.size() > 0)
                {
                    allDrops.addAll(drops);
                }

                block.onBlockExploded(worldObj, blockpos, this);
            }
        }
		if (getExplosivePlacedBy() != null)
        {
            WorldHelper.createLootDrop(allDrops, this.worldObj, new BlockPos(getExplosivePlacedBy()));
        }
        else
        {
            WorldHelper.createLootDrop(allDrops, this.worldObj, x, y, z);
        }
	}
}
