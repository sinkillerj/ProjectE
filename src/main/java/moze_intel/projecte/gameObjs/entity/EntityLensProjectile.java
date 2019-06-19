package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.ServerWorld;

import javax.annotation.Nonnull;

public class EntityLensProjectile extends ThrowableEntity implements IRendersAsItem
{
	private int charge;
	
	public EntityLensProjectile(EntityType<EntityLensProjectile> type, World world)
	{
		super(type, world);
	}

	public EntityLensProjectile(PlayerEntity entity, int charge, World world)
	{
		super(ObjHandler.LENS_PROJECTILE, entity, world);
		this.charge = charge;
	}

	@Override
	protected void registerData() {}

	@Override
	public void tick()
	{
		super.tick();
		
		if (this.getEntityWorld().isRemote)
		{
			return;
		}

		if (ticksExisted > 400 || !this.getEntityWorld().isBlockLoaded(new BlockPos(this)))
		{
			this.remove();
			return;
		}

		if (this.isInWater())
		{
			this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
			((ServerWorld) world).spawnParticle(ParticleTypes.LARGE_SMOKE, posX, posY, posZ, 2, 0, 0, 0, 0);
			this.remove();
		}
	}

	@Override
	protected void onImpact(RayTraceResult mop)
	{
		if (!this.getEntityWorld().isRemote)
		{
			WorldHelper.createNovaExplosion(world, getThrower(), posX, posY, posZ, Constants.EXPLOSIVE_LENS_RADIUS[charge]);
			remove();
		}
	}

	@Override
	public float getGravityVelocity()
	{
		return 0;
	}

	@Override
	public void writeAdditional(CompoundNBT nbt)
	{
		super.writeAdditional(nbt);
		nbt.putInt("Charge", charge);
	}

	@Override
	public void readAdditional(CompoundNBT nbt)
	{
		super.readAdditional(nbt);
		charge = nbt.getInt("Charge");
	}

	@Nonnull
	@Override
	public ItemStack getItem()
	{
		return new ItemStack(ObjHandler.lensExplosive);
	}
}
