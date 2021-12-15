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
	public float getGravity() {
		return 0;
	}

	@Override
	protected void onHit(@Nonnull RayTraceResult mop) {
		Entity thrower = getOwner();
		if (!level.isClientSide && thrower instanceof PlayerEntity && mop instanceof BlockRayTraceResult) {
			BlockPos pos = ((BlockRayTraceResult) mop).getBlockPos();
			Block block = level.getBlockState(pos).getBlock();
			if (block == Blocks.OBSIDIAN) {
				level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
			} else if (block == Blocks.SAND) {
				BlockPos.betweenClosedStream(pos.offset(-2, -2, -2), pos.offset(2, 2, 2)).forEach(currentPos -> {
					if (level.getBlockState(currentPos).getBlock() == Blocks.SAND) {
						PlayerHelper.checkedPlaceBlock((ServerPlayerEntity) thrower, pos.immutable(), Blocks.GLASS.defaultBlockState());
					}
				});
			} else {
				BlockPos.betweenClosedStream(pos.offset(-1, -1, -1), pos.offset(1, 1, 1)).forEach(currentPos -> {
					if (level.isEmptyBlock(currentPos)) {
						PlayerHelper.checkedPlaceBlock((ServerPlayerEntity) thrower, currentPos.immutable(), Blocks.FIRE.defaultBlockState());
					}
				});
			}
		}
		if (!level.isClientSide) {
			remove();
		}
	}

	@Override
	protected void defineSynchedData() {
	}

	@Nonnull
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}
}