package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class EntityLavaProjectile extends PEProjectile
{
	public EntityLavaProjectile(World world) 
	{
		super(world);
	}

	public EntityLavaProjectile(World world, EntityPlayer entity)
	{
		super(world, entity);
	}

	public EntityLavaProjectile(World world, double x, double y, double z) 
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

			boolean flag = true;

			for (BlockPos pos : WorldHelper.getPositionsFromCorners(this.getPosition().add(-3, -3, -3), this.getPosition().add(3, 3, 3)))
			{
				Block block = this.worldObj.getBlockState(pos).getBlock();

				if (block == Blocks.water || block == Blocks.flowing_water)
				{
					this.worldObj.setBlockToAir(pos);

					if (flag)
					{
						this.worldObj.playSoundAtEntity(this, "random.fizz", 0.5F, 2.6F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8F);
						flag = false;
					}
				}
			}

			if (this.posY > 128)
			{
				WorldInfo worldInfo = this.worldObj.getWorldInfo();
				worldInfo.setRaining(false);
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

		if (tryConsumeEmc(((ItemPE) ObjHandler.volcanite), 32))
		{
			switch (mop.typeOfHit)
			{
				case BLOCK:
					this.worldObj.setBlockState(mop.getBlockPos().offset(mop.sideHit), Blocks.flowing_lava.getDefaultState());
					break;
				case ENTITY:
					Entity ent = mop.entityHit;
					ent.setFire(5);
					ent.attackEntityFrom(DamageSource.inFire, 5);
			}
		}
	}
}
