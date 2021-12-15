package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityLavaProjectile extends ThrowableEntity {

	public EntityLavaProjectile(EntityType<EntityLavaProjectile> type, World world) {
		super(type, world);
	}

	public EntityLavaProjectile(PlayerEntity entity, World world) {
		super(PEEntityTypes.LAVA_PROJECTILE.get(), entity, world);
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide) {
			if (tickCount > 400 || !level.isLoaded(blockPosition())) {
				remove();
				return;
			}
			Entity thrower = getOwner();
			if (thrower instanceof ServerPlayerEntity) {
				ServerPlayerEntity player = (ServerPlayerEntity) thrower;
				BlockPos.betweenClosedStream(blockPosition().offset(-3, -3, -3), blockPosition().offset(3, 3, 3)).forEach(pos -> {
					if (level.isLoaded(pos)) {
						BlockState state = level.getBlockState(pos);
						if (state.getFluidState().getType().is(FluidTags.WATER)) {
							pos = pos.immutable();
							if (PlayerHelper.hasEditPermission(player, pos)) {
								WorldHelper.drainFluid(level, pos, state, Fluids.WATER);
								level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
										2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
							}
						}
					}
				});
			}
			if (getY() > 128) {
				IWorldInfo worldInfo = level.getLevelData();
				worldInfo.setRaining(false);
				remove();
			}
		}
	}

	@Override
	public float getGravity() {
		return 0;
	}

	@Override
	protected void onHit(@Nonnull RayTraceResult mop) {
		if (level.isClientSide) {
			return;
		}
		Entity thrower = getOwner();
		if (!(thrower instanceof PlayerEntity)) {
			remove();
			return;
		}
		PlayerEntity player = (PlayerEntity) thrower;
		ItemStack found = PlayerHelper.findFirstItem(player, PEItems.VOLCANITE_AMULET.get());
		if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 32, true)) {
			if (mop instanceof BlockRayTraceResult) {
				BlockRayTraceResult result = (BlockRayTraceResult) mop;
				WorldHelper.placeFluid((ServerPlayerEntity) player, level, result.getBlockPos(), result.getDirection(), Fluids.LAVA, false);
			} else if (mop instanceof EntityRayTraceResult) {
				Entity ent = ((EntityRayTraceResult) mop).getEntity();
				ent.setSecondsOnFire(5);
				ent.hurt(DamageSource.IN_FIRE, 5);
			}
		}
		remove();
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