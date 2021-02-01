package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityFireProjectile extends ThrowableEntity {

	public EntityFireProjectile(EntityType<EntityFireProjectile> type, World world) {
		super(type, world);
	}

	public EntityFireProjectile(PlayerEntity entity, World world) {
		super(PEEntityTypes.FIRE_PROJECTILE.get(), entity, world);
	}

	@Override
	public float getGravityVelocity() {
		return 0;
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult mop) {
		Entity thrower = func_234616_v_();
		if (!world.isRemote && thrower instanceof PlayerEntity && mop instanceof BlockRayTraceResult) {
			BlockPos pos = ((BlockRayTraceResult) mop).getPos();
			Block block = world.getBlockState(pos).getBlock();
			if (block == Blocks.OBSIDIAN) {
				world.setBlockState(pos, Blocks.LAVA.getDefaultState());
			} else if (block == Blocks.SAND) {
				BlockPos.getAllInBox(pos.add(-2, -2, -2), pos.add(2, 2, 2)).forEach(currentPos -> {
					if (world.getBlockState(currentPos).getBlock() == Blocks.SAND) {
						PlayerHelper.checkedPlaceBlock((ServerPlayerEntity) thrower, pos.toImmutable(), Blocks.GLASS.getDefaultState());
					}
				});
			} else {
				BlockPos.getAllInBox(pos.add(-1, -1, -1), pos.add(1, 1, 1)).forEach(currentPos -> {
					if (world.isAirBlock(currentPos)) {
						PlayerHelper.checkedPlaceBlock((ServerPlayerEntity) thrower, currentPos.toImmutable(), Blocks.FIRE.getDefaultState());
					}
				});
			}
		}
		if (!world.isRemote) {
			remove();
		}
	}

	@Override
	protected void registerData() {
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}
}