package moze_intel.projecte.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NovaExplosion extends Explosion 
{
	private World worldObj;
	
	NovaExplosion(World world, Entity entity, double x, double y, double z, float radius)
	{
		super(world, entity, x, y, z, radius);
		isFlaming = true;
		isSmoking = true;
		worldObj = world;
	}
	
	@Override
	public void doExplosionA()
	{
		float f = this.explosionSize;
		Set<ChunkPosition> hashset = Sets.newHashSet();
		int i;
		int j;
		int k;
		double d5;
		double d6;
		double d7;

		for (i = 0; i < 16; ++i)
			for (j = 0; j < 16; ++j)
				for (k = 0; k < 16; ++k)
					if (i == 0 || i == 16 - 1 || j == 0 || j == 16 - 1 || k == 0 || k == 16 - 1)
					{
						double d0 = (double)((float)i / ((float)16 - 1.0F) * 2.0F - 1.0F);
						double d1 = (double)((float)j / ((float)16 - 1.0F) * 2.0F - 1.0F);
						double d2 = (double)((float)k / ((float)16 - 1.0F) * 2.0F - 1.0F);
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 /= d3;
						d1 /= d3;
						d2 /= d3;
						float f1 = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
						d5 = this.explosionX;
						d6 = this.explosionY;
						d7 = this.explosionZ;

						for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F)
						{
							int j1 = MathHelper.floor_double(d5);
							int k1 = MathHelper.floor_double(d6);
							int l1 = MathHelper.floor_double(d7);
							Block block = this.worldObj.getBlock(j1, k1, l1);

							if (block.getMaterial() != Material.air)
							{
								float f3 = this.exploder != null ? this.exploder.func_145772_a(this, this.worldObj, j1, k1, l1, block) : block.getExplosionResistance(this.exploder, worldObj, j1, k1, l1, explosionX, explosionY, explosionZ);
								f1 -= (f3 + 0.3F) * f2;
							}

							if (f1 > 0.0F && (this.exploder == null || this.exploder.func_145774_a(this, this.worldObj, j1, k1, l1, block, f1)))
								hashset.add(new ChunkPosition(j1, k1, l1));

							d5 += d0 * (double)f2;
							d6 += d1 * (double)f2;
							d7 += d2 * (double)f2;
						}
					}

		this.affectedBlockPositions.addAll(hashset);
		this.explosionSize = f;
		net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.worldObj, this, Collections.<Entity>emptyList(), this.explosionSize);
	}
	
	@Override
	public void doExplosionB(boolean spawnParticles)
	{
		worldObj.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		if (this.explosionSize >= 2.0F && this.isSmoking)
			worldObj.spawnParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
		else
			worldObj.spawnParticle("largeexplode", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);

		Iterator iterator;
		ChunkPosition chunkposition;
		int i;
		int j;
		int k;
		Block block;
		List<ItemStack> list = Lists.newArrayList();
		

		if (this.isSmoking)
		{
			iterator = this.affectedBlockPositions.iterator();

			while (iterator.hasNext())
			{
				chunkposition = (ChunkPosition)iterator.next();
				i = chunkposition.chunkPosX;
				j = chunkposition.chunkPosY;
				k = chunkposition.chunkPosZ;
				block = worldObj.getBlock(i, j, k);

				if (spawnParticles)
				{
					double d0 = (double) ((float) i + worldObj.rand.nextFloat());
					double d1 = (double) ((float) j + worldObj.rand.nextFloat());
					double d2 = (double) ((float) k + worldObj.rand.nextFloat());
					double d3 = d0 - this.explosionX;
					double d4 = d1 - this.explosionY;
					double d5 = d2 - this.explosionZ;
					double d6 = (double) MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
					d3 /= d6;
					d4 /= d6;
					d5 /= d6;
					double d7 = 0.5D / (d6 / (double) this.explosionSize + 0.1D);
					d7 *= (double) (worldObj.rand.nextFloat() * worldObj.rand.nextFloat() + 0.3F);
					d3 *= d7;
					d4 *= d7;
					d5 *= d7;
					worldObj.spawnParticle("explode", (d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D, (d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5);
					worldObj.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
				}

				if (block.getMaterial() != Material.air)
				{
					ArrayList<ItemStack> drops = block.getDrops(worldObj, i, j, k, worldObj.getBlockMetadata(i, j, k), 0);
					if (drops != null && drops.size() > 0)
						list.addAll(drops);

					block.onBlockExploded(worldObj, i, j, k, this);
				}
			}
			
			Entity ent = this.getExplosivePlacedBy();
			if (ent != null) {
				WorldHelper.createLootDrop(list, worldObj, ent.posX, ent.posY, ent.posZ);
			} else {
				WorldHelper.createLootDrop(list, worldObj, explosionX, explosionY, explosionZ);
			}
		}
	}
}
