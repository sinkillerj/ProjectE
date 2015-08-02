package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.util.ForgeDirection;

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
			if (ticksExisted > 400 || !this.worldObj.blockExists(((int) this.posX), ((int) this.posY), ((int) this.posZ)))
			{
				this.setDead();
				return;
			}

			for (int x = (int) (this.posX - 3); x <= this.posX + 3; x++)
				for (int y = (int) (this.posY - 3); y <= this.posY + 3; y++)
					for (int z = (int) (this.posZ - 3); z <= this.posZ + 3; z++)
					{
						Block block = this.worldObj.getBlock(x, y, z);

						if (block == Blocks.lava)
						{
							this.worldObj.setBlock(x, y, z, Blocks.obsidian);
						}
						else if (block == Blocks.flowing_lava)
						{
							this.worldObj.setBlock(x, y, z, Blocks.cobblestone);
						}
						else
						{
							continue;
						}
						
						this.worldObj.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8F);
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
			ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
			int x = mop.blockX + dir.offsetX;
			int y = mop.blockY + dir.offsetY;
			int z = mop.blockZ + dir.offsetZ;
			if (worldObj.isAirBlock(x, y, z) && PlayerHelper.hasEditPermission(worldObj, ((EntityPlayerMP) getThrower()), x, y, z))
			{
				this.worldObj.setBlock(x, y, z, Blocks.flowing_water);
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
