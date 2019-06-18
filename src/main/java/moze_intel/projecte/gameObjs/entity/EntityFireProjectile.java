package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityFireProjectile extends ThrowableEntity
{
	public EntityFireProjectile(EntityType<EntityFireProjectile> type, World world)
	{
		super(type, world);
	}

	public EntityFireProjectile(PlayerEntity entity, World world)
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
		if(!world.isRemote && getThrower() instanceof PlayerEntity && mop.type == RayTraceResult.Type.BLOCK)
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
						PlayerHelper.checkedPlaceBlock(((ServerPlayerEntity) getThrower()), pos, Blocks.GLASS.getDefaultState());
					}
				}
			}
			else
			{
				for (BlockPos currentPos : BlockPos.getAllInBox(pos.add(-1, -1, -1), mop.getBlockPos().add(1, 1, 1)))
				{
					if(world.isAirBlock(currentPos))
					{
						PlayerHelper.checkedPlaceBlock(((ServerPlayerEntity) getThrower()), currentPos, Blocks.FIRE.getDefaultState());
					}
				}
			}
		}
		if (!world.isRemote)
		{
			remove();
		}
	}

	@Override
	protected void registerData() {}
}
