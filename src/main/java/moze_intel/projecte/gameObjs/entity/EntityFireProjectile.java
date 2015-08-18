package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityFireProjectile extends PEProjectile
{
	public EntityFireProjectile(World world)
	{
		super(world);
	}

	public EntityFireProjectile(World world, EntityPlayer entity)
	{
		super(world, entity);
	}

	public EntityFireProjectile(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}

	@Override
	protected void apply(MovingObjectPosition mop)
	{
		if(!worldObj.isRemote && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			BlockPos pos = mop.getBlockPos();
			Block block = worldObj.getBlockState(pos).getBlock();
			
			if(block == Blocks.obsidian)
			{
				worldObj.setBlockState(pos, Blocks.flowing_lava.getDefaultState());
			}
			else if(block == Blocks.sand)
			{
				for (BlockPos currentPos : WorldHelper.getPositionsFromCorners(pos.add(-2, -2, -2), mop.getBlockPos().add(2, 2, 2)))
				{
					if(worldObj.getBlockState(currentPos).getBlock() == Blocks.sand)
					{
						PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) getThrower()), pos, Blocks.glass.getDefaultState());
					}
				}
			}
			else
			{
				for (BlockPos currentPos : WorldHelper.getPositionsFromCorners(pos.add(-1, -1, -1), mop.getBlockPos().add(1, 1, 1)))
				{
					if(worldObj.isAirBlock(currentPos))
					{
						PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) getThrower()), pos, Blocks.fire.getDefaultState());
					}
				}
			}
		}
	}
}
