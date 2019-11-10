package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.network.IPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityWaterProjectile extends ThrowableEntity {

	public EntityWaterProjectile(EntityType<EntityWaterProjectile> type, World world) {
		super(type, world);
	}

	public EntityWaterProjectile(PlayerEntity entity, World world) {
		super(ObjHandler.WATER_PROJECTILE, entity, world);
	}

	@Override
	protected void registerData() {
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.getEntityWorld().isRemote) {
			if (ticksExisted > 400 || !this.getEntityWorld().isBlockLoaded(new BlockPos(this))) {
				this.remove();
				return;
			}
			if (getThrower() instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) getThrower();
				BlockPos.getAllInBox(this.getPosition().add(-3, -3, -3), this.getPosition().add(3, 3, 3)).forEach(pos -> {
					IFluidState state = this.getEntityWorld().getFluidState(pos);
					if (state.isTagged(FluidTags.LAVA)) {
						pos = pos.toImmutable();
						if (state.isSource()) {
							PlayerHelper.checkedReplaceBlock(player, pos, Blocks.OBSIDIAN.getDefaultState());
						} else {
							PlayerHelper.checkedReplaceBlock(player, pos, Blocks.COBBLESTONE.getDefaultState());
						}
						playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.5F, 2.6F + (this.getEntityWorld().rand.nextFloat() - this.getEntityWorld().rand.nextFloat()) * 0.8F);
					}
				});
			}
			if (this.isInWater()) {
				this.remove();
			}
			if (this.posY > 128) {
				WorldInfo worldInfo = this.getEntityWorld().getWorldInfo();
				worldInfo.setRaining(true);
				this.remove();
			}
		}
	}

	@Override
	public float getGravityVelocity() {
		return 0;
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult mop) {
		if (world.isRemote) {
			return;
		}
		if (!(getThrower() instanceof PlayerEntity)) {
			remove();
			return;
		}
		if (mop instanceof BlockRayTraceResult) {
			BlockRayTraceResult result = (BlockRayTraceResult) mop;
			WorldHelper.placeFluid((ServerPlayerEntity) getThrower(), world, result.getPos(), result.getFace(), Fluids.WATER, !ProjectEConfig.server.items.opEvertide.get());
		} else if (mop instanceof EntityRayTraceResult) {
			Entity ent = ((EntityRayTraceResult) mop).getEntity();
			if (ent.isBurning()) {
				ent.extinguish();
			}
			ent.addVelocity(this.getMotion().getX() * 2, this.getMotion().getY() * 2, this.getMotion().getZ() * 2);
		}
		remove();
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}