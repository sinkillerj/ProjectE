package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class EntityWaterProjectile extends EntityThrowable
{
	public EntityWaterProjectile(World world)
	{
		super(ObjHandler.WATER_PROJECTILE, world);
	}

	public EntityWaterProjectile(EntityPlayer entity, World world)
	{
		super(ObjHandler.WATER_PROJECTILE, entity, world);
	}

	@Override
	public void tick()
	{
		super.tick();

		if (!this.getEntityWorld().isRemote)
		{
			if (ticksExisted > 400 || !this.getEntityWorld().isBlockLoaded(new BlockPos(this)))
			{
				this.remove();
				return;
			}

			if (getThrower() instanceof EntityPlayerMP) {
				EntityPlayerMP player = ((EntityPlayerMP) getThrower());

				for (BlockPos pos : BlockPos.getAllInBox(this.getPosition().add(-3, -3, -3), this.getPosition().add(3, 3, 3)))
                {
					IFluidState state = this.getEntityWorld().getFluidState(pos);

					if (state.isTagged(FluidTags.LAVA))
					{
						if (state.isSource())
						{
							PlayerHelper.checkedReplaceBlock(player, pos, Blocks.OBSIDIAN.getDefaultState());
						} else
						{
							PlayerHelper.checkedReplaceBlock(player, pos, Blocks.COBBLESTONE.getDefaultState());
						}
						playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.5F, 2.6F + (this.getEntityWorld().rand.nextFloat() - this.getEntityWorld().rand.nextFloat()) * 0.8F);
					}
                }
			}

			if (this.isInWater())
			{
				this.remove();
			}
			
			if (this.posY > 128)
			{
				WorldInfo worldInfo = this.getEntityWorld().getWorldInfo();
				worldInfo.setRaining(true);
				this.remove();
			}
		}
	}

	@Override
	public float getGravityVelocity()
	{
		return 0;
	}

	@Override
	protected void onImpact(RayTraceResult mop)
	{
		if (this.getEntityWorld().isRemote)
		{
			return;
		}

		if (!(getThrower() instanceof EntityPlayer))
		{
			remove();
			return;
		}

		if (mop.type == Type.BLOCK)
		{
			BlockPos pos = mop.getBlockPos().offset(mop.sideHit);
			if (world.isAirBlock(pos))
			{
				PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) getThrower()), pos, Blocks.WATER.getDefaultState());
			}
		}
		else if (mop.type == Type.ENTITY)
		{
			Entity ent = mop.entity;

			if (ent.isBurning())
			{
				ent.extinguish();
			}

			ent.addVelocity(this.motionX * 2, this.motionY * 2, this.motionZ * 2);
		}

		remove();
	}
}
