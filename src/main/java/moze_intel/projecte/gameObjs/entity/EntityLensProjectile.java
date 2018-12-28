package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityLensProjectile extends EntityThrowable
{
	private int charge;
	
	public EntityLensProjectile(World world) 
	{
		super(ObjHandler.LENS_PROJECTILE, world);
	}

	public EntityLensProjectile(EntityPlayer entity, int charge, World world)
	{
		super(ObjHandler.LENS_PROJECTILE, entity, world);
		this.charge = charge;
	}

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
			((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX, posY, posZ, 2, 0, 0, 0, 0, new int[0]);
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
	public void writeAdditional(NBTTagCompound nbt)
	{
		super.writeAdditional(nbt);
		nbt.putInt("Charge", charge);
	}

	@Override
	public void readAdditional(NBTTagCompound nbt)
	{
		super.readAdditional(nbt);
		charge = nbt.getInt("Charge");
	}
}
