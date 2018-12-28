package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityFireProjectile extends EntityThrowable
{
	public EntityFireProjectile(World world)
	{
		super(ObjHandler.FIRE_PROJECTILE, world);
	}

	public EntityFireProjectile(EntityPlayer entity, World world)
	{
		super(ObjHandler.FIRE_PROJECTILE, entity, world);
	}

	@Override
	public float getGravityVelocity()
	{
		return 0;
	}

	@Override
	protected void onImpact(RayTraceResult mop)
	{
		if(!world.isRemote && getThrower() instanceof EntityPlayer && mop.type == RayTraceResult.Type.BLOCK)
		{
			BlockPos pos = mop.getBlockPos();
			Block block = world.getBlockState(pos).getBlock();
			
			if(block == Blocks.OBSIDIAN)
			{
				world.setBlockState(pos, Blocks.LAVA.getDefaultState());
			}
			else if(block == Blocks.SAND)
			{
				for (BlockPos currentPos : BlockPos.getAllInBox(pos.add(-2, -2, -2), mop.getBlockPos().add(2, 2, 2)))
				{
					if(world.getBlockState(currentPos).getBlock() == Blocks.SAND)
					{
						PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) getThrower()), pos, Blocks.GLASS.getDefaultState());
					}
				}
			}
			else
			{
				for (BlockPos currentPos : BlockPos.getAllInBox(pos.add(-1, -1, -1), mop.getBlockPos().add(1, 1, 1)))
				{
					if(world.isAirBlock(currentPos))
					{
						PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) getThrower()), currentPos, Blocks.FIRE.getDefaultState());
					}
				}
			}
		}
		if (!world.isRemote)
		{
			remove();
		}
	}
}
