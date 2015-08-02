package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.util.ForgeDirection;

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
			if (ticksExisted > 400 || !this.worldObj.blockExists(((int) this.posX), ((int) this.posY), ((int) this.posZ)))
			{
				this.setDead();
				return;
			}

			boolean flag = true;

			for (int x = (int) (this.posX - 3); x <= this.posX + 3; x++)
				for (int y = (int) (this.posY - 3); y <= this.posY + 3; y++)
					for (int z = (int) (this.posZ - 3); z <= this.posZ + 3; z++)
					{
						Block block = this.worldObj.getBlock(x, y, z);

						if (block == Blocks.water || block == Blocks.flowing_water)
						{
							this.worldObj.setBlockToAir(x, y, z);

							if (flag)
							{
								this.worldObj.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "random.fizz", 0.5F, 2.6F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8F);
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
					ForgeDirection dir = ForgeDirection.getOrientation(mop.sideHit);
					int x = mop.blockX + dir.offsetX;
					int y = mop.blockY + dir.offsetY;
					int z = mop.blockZ + dir.offsetZ;
					if (worldObj.isAirBlock(x, y, z) && PlayerHelper.hasEditPermission(worldObj, ((EntityPlayerMP) getThrower()), x, y, z))
					{
						this.worldObj.setBlock(x, y, z, Blocks.flowing_lava);
					}
					break;
				case ENTITY:
					Entity ent = mop.entityHit;
					ent.setFire(5);
					ent.attackEntityFrom(DamageSource.inFire, 5);
			}
		}
	}
}
