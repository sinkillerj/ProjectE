package moze_intel.projecte.gameObjs.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
			int x = mop.blockX;
			int y = mop.blockY;
			int z = mop.blockZ;
			
			Block block = worldObj.getBlock(x, y, z);
			
			if(block == Blocks.obsidian)
			{
				worldObj.setBlock(x, y, z, Blocks.flowing_lava);
			}
			else if(block == Blocks.sand)
			{
				for(int x1 = x - 2; x1 <= x + 2; x1++)
					for(int y1 = y - 2; y1 <= y + 2; y1++)
						for(int z1 = z - 2; z1 <= z + 2; z1++)
							if(worldObj.getBlock(x1, y1, z1) == Blocks.sand)
								worldObj.setBlock(x1, y1, z1, Blocks.glass);
			}
			else
			{
				for(int x1 = x - 1; x1 <= x + 1; x1++)
					for(int y1 = y - 1; y1 <= y + 1; y1++)
						for(int z1 = z - 1; z1 <= z + 1; z1++)
							if(worldObj.getBlock(x1, y1, z1) == Blocks.air)
								worldObj.setBlock(x1, y1, z1, Blocks.fire);
			}
		}
	}
}
