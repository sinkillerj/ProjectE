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

			if (getThrower() instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = ((EntityPlayerMP) getThrower());
				for (int x = (int) (this.posX - 3); x <= this.posX + 3; x++)
					for (int y = (int) (this.posY - 3); y <= this.posY + 3; y++)
						for (int z = (int) (this.posZ - 3); z <= this.posZ + 3; z++)
						{
							Block block = this.worldObj.getBlock(x, y, z);

							if (block == Blocks.water || block == Blocks.flowing_water)
							{
								if (PlayerHelper.hasBreakPermission(player, x, y, z))
								{
									this.worldObj.setBlockToAir(x, y, z);
									this.worldObj.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.fizz", 0.5F, 2.6F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.8F);
								}
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
					PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) getThrower()), mop.blockX + dir.offsetX, mop.blockY + dir.offsetY, mop.blockZ + dir.offsetZ, Blocks.flowing_lava, 0);
					break;
				case ENTITY:
					Entity ent = mop.entityHit;
					ent.setFire(5);
					ent.attackEntityFrom(DamageSource.inFire, 5);
			}
		}
	}
}
