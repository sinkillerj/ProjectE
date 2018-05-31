package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
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

		if (!this.getEntityWorld().isRemote)
		{
			if (ticksExisted > 400 || !this.getEntityWorld().isBlockLoaded(new BlockPos(this)))
			{
				this.setDead();
				return;
			}

			if (getThrower() instanceof EntityPlayerMP) {
				EntityPlayerMP player = ((EntityPlayerMP) getThrower());

				for (BlockPos pos : BlockPos.getAllInBox(this.getPosition().add(-3, -3, -3), this.getPosition().add(3, 3, 3)))
                {
                    Block block = this.getEntityWorld().getBlockState(pos).getBlock();

                    if (block == Blocks.LAVA)
                    {
                        PlayerHelper.checkedReplaceBlock(player, pos, Blocks.OBSIDIAN.getDefaultState());
                    }
                    else if (block == Blocks.FLOWING_LAVA)
                    {
                        PlayerHelper.checkedReplaceBlock(player, pos, Blocks.COBBLESTONE.getDefaultState());
                    }
                    else
                    {
                        continue;
                    }

                    playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.5F, 2.6F + (this.getEntityWorld().rand.nextFloat() - this.getEntityWorld().rand.nextFloat()) * 0.8F);
                }
			}

			if (this.isInWater())
			{
				this.setDead();
			}
			
			if (this.posY > 128)
			{
				WorldInfo worldInfo = this.getEntityWorld().getWorldInfo();
				worldInfo.setRaining(true);
				this.setDead();
			}
		}
	}

	@Override
	protected void apply(RayTraceResult mop)
	{
		if (this.getEntityWorld().isRemote)
		{
			return;
		}

		if (mop.typeOfHit == Type.BLOCK)
		{
			BlockPos pos = mop.getBlockPos().offset(mop.sideHit);
			if (world.isAirBlock(pos))
			{
				PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) getThrower()), pos, Blocks.FLOWING_WATER.getDefaultState());
			}
		}
		else if (mop.typeOfHit == Type.ENTITY)
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
