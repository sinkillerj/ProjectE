package moze_intel.projecte.gameObjs.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
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
				for(int x1 = pos.getX() - 2; x1 <= pos.getX() + 2; x1++)
					for(int y1 = pos.getY() - 2; y1 <= pos.getY() + 2; y1++)
						for(int z1 = pos.getZ() - 2; z1 <= pos.getZ() + 2; z1++)
						{
							BlockPos currentPos = new BlockPos(x1, y1, z1);
							if(worldObj.getBlockState(currentPos) == Blocks.sand)
								worldObj.setBlockState(currentPos, Blocks.glass.getDefaultState());
						}
			}
			else
			{
				for(int x1 = pos.getX() - 1; x1 <= pos.getX() + 1; x1++)
					for(int y1 = pos.getY() - 1; y1 <= pos.getY() + 1; y1++)
						for(int z1 = pos.getZ() - 1; z1 <= pos.getZ() + 1; z1++)
						{
							BlockPos currentPos = new BlockPos(x1, y1, z1);
							if(worldObj.isAirBlock(currentPos))
								worldObj.setBlockState(currentPos, Blocks.fire.getDefaultState());
						}
			}
		}
	}
}
