package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class EntityWaterProjectile extends PEProjectile
{
	public EntityWaterProjectile(World world)
	{
		super(world);
	}

	public EntityWaterProjectile(World world, EntityPlayer entity)
	{
		super(world, entity);
	}

	public EntityWaterProjectile(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if (!this.worldObj.isRemote)
		{
			if (ticksExisted > 400 || !this.worldObj.isBlockLoaded(new BlockPos(this)))
			{
				this.setDead();
				return;
			}

			for (BlockPos pos : WorldHelper.getPositionsFromCorners(this.getPosition().add(-3, -3, -3), this.getPosition().add(3, 3, 3)))
			{
				Block block = this.worldObj.getBlockState(pos).getBlock();
				boolean flag = false;

				if (block == Blocks.lava)
				{
					this.worldObj.setBlockState(pos, Blocks.obsidian.getDefaultState());
				}
				else if (block == Blocks.flowing_lava)
				{
					this.worldObj.setBlockState(pos, Blocks.cobblestone.getDefaultState());
				}
				else
				{
					continue;
				}

				this.worldObj.playSoundAtEntity(this, "random.fizz", 0.5F, 2.6F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8F);
			}

			if (this.isInWater())
			{
				this.setDead();
			}
			
			if (this.posY > 128)
			{
				WorldInfo worldInfo = this.worldObj.getWorldInfo();
				worldInfo.setRaining(true);
				this.setDead();
			}
		}
	}

	@Override
	protected void apply(MovingObjectPosition mop)
	{
		if (this.worldObj.isRemote)
		{
			return;
		}

		if (mop.typeOfHit == MovingObjectType.BLOCK)
		{
			BlockPos pos = mop.getBlockPos().offset(mop.sideHit);
			if (worldObj.isAirBlock(pos))
			{
				this.worldObj.setBlockState(pos, Blocks.flowing_water.getDefaultState());
			}
		}
		else if (mop.typeOfHit == MovingObjectType.ENTITY)
		{
			Entity ent = mop.entityHit;

			if (ent.isBurning())
			{
				ent.extinguish();
			}

			ent.addVelocity(this.motionX * 2, this.motionY * 2, this.motionZ * 2);
		}
	}
}
