package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nonnull;

public class EntityLavaProjectile extends ThrowableEntity implements IRendersAsItem
{
	public EntityLavaProjectile(EntityType<EntityLavaProjectile> type, World world)
	{
		super(type, world);
	}

	public EntityLavaProjectile(PlayerEntity entity, World world)
	{
		super(ObjHandler.LAVA_PROJECTILE, entity, world);
	}

	@Override
	protected void registerData() {}

	@Override
	public void tick()
	{
		super.tick();
		
		if (!this.getEntityWorld().isRemote)
		{
			if (ticksExisted > 400 || !this.getEntityWorld().isBlockLoaded(new BlockPos(this)))
			{
				this.remove();
				return;
			}

			if (getThrower() instanceof ServerPlayerEntity)
			{
				ServerPlayerEntity player = ((ServerPlayerEntity) getThrower());
				BlockPos.getAllInBox(this.getPosition().add(-3, -3, -3), this.getPosition().add(3, 3, 3)).forEach(pos -> {
					Block block = this.getEntityWorld().getBlockState(pos).getBlock();
					if (block == Blocks.WATER)
					{
						if (PlayerHelper.hasBreakPermission(player, pos))
						{
							this.getEntityWorld().removeBlock(pos, false);
							this.getEntityWorld().playSound(null, pos, SoundEvents.ENTITY_BLAZE_BURN, SoundCategory.BLOCKS, 0.5F, 2.6F + (this.getEntityWorld().rand.nextFloat() - this.getEntityWorld().rand.nextFloat()) * 0.8F);
						}
					}
				});
			}

			if (this.posY > 128)
			{
				WorldInfo worldInfo = this.getEntityWorld().getWorldInfo();
				worldInfo.setRaining(false);
				this.remove();
			}
		}
	}

	@Override
	public float getGravityVelocity()
	{
		return 0;
	}

	@Override
	protected void onImpact(RayTraceResult mop)
	{
		if (!this.getEntityWorld().isRemote || getThrower() instanceof PlayerEntity)
		{
			PlayerEntity player = ((PlayerEntity) getThrower());
			ItemStack found = PlayerHelper.findFirstItem(player, ObjHandler.volcanite);
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 32, true))
			{
				if (mop instanceof BlockRayTraceResult)
				{
					BlockRayTraceResult brtr = (BlockRayTraceResult) mop;
					PlayerHelper.checkedPlaceBlock(((ServerPlayerEntity) getThrower()), brtr.getPos().offset(brtr.getFace()), Blocks.LAVA.getDefaultState());
				} else if (mop instanceof EntityRayTraceResult)
				{
					Entity ent = ((EntityRayTraceResult) mop).getEntity();
					ent.setFire(5);
					ent.attackEntityFrom(DamageSource.IN_FIRE, 5);
				}
			}
		}

		if (!world.isRemote)
		{
			remove();
		}
	}

	@Nonnull
	@Override
	public ItemStack getItem()
	{
		return new ItemStack(ObjHandler.lavaOrb);
	}
}
