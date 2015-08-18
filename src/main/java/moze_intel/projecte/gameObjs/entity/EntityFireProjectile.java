package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
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
				PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) getThrower()), x, y, z, Blocks.flowing_lava, 0);
			}
			else
			{
				for(int x1 = x - 1; x1 <= x + 1; x1++)
					for(int y1 = y - 1; y1 <= y + 1; y1++)
						for(int z1 = z - 1; z1 <= z + 1; z1++)
						{
							if(worldObj.isAirBlock(x1, y1, z1))
							{
								PlayerHelper.checkedPlaceBlock(((EntityPlayerMP) getThrower()), x1, y1, z1, Blocks.fire, 0);
							} else if (worldObj.getBlock(x1, y1, z1) == Blocks.sand)
							{
								PlayerHelper.checkedReplaceBlock(((EntityPlayerMP) getThrower()), x1, y1, z1, Blocks.glass, 0);
							}

						}
			}
		}
	}
}
