package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class EntityFireProjectile extends ThrowableProjectile {

	public EntityFireProjectile(EntityType<EntityFireProjectile> type, Level level) {
		super(type, level);
	}

	public EntityFireProjectile(Player entity, Level level) {
		super(PEEntityTypes.FIRE_PROJECTILE.get(), entity, level);
	}

	@Override
	public float getGravity() {
		return 0;
	}

	@Override
	protected void onHit(@Nonnull HitResult mop) {
		Entity thrower = getOwner();
		if (!level.isClientSide && thrower instanceof Player && mop instanceof BlockHitResult result) {
			BlockPos pos = result.getBlockPos();
			Block block = level.getBlockState(pos).getBlock();
			if (block == Blocks.OBSIDIAN) {
				level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
			} else if (block == Blocks.SAND) {
				BlockPos.betweenClosedStream(pos.offset(-2, -2, -2), pos.offset(2, 2, 2)).forEach(currentPos -> {
					if (level.getBlockState(currentPos).getBlock() == Blocks.SAND) {
						PlayerHelper.checkedPlaceBlock((ServerPlayer) thrower, pos.immutable(), Blocks.GLASS.defaultBlockState());
					}
				});
			} else {
				BlockPos.betweenClosedStream(pos.offset(-1, -1, -1), pos.offset(1, 1, 1)).forEach(currentPos -> {
					if (level.isEmptyBlock(currentPos)) {
						PlayerHelper.checkedPlaceBlock((ServerPlayer) thrower, currentPos.immutable(), Blocks.FIRE.defaultBlockState());
					}
				});
			}
		}
		if (!level.isClientSide) {
			discard();
		}
	}

	@Override
	protected void defineSynchedData() {
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}
}