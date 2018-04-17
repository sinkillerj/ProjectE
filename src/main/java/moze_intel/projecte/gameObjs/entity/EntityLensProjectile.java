package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityLensProjectile extends PEProjectile
{
	private int charge;
	
	public EntityLensProjectile(World world) 
	{
		super(world);
	}

	public EntityLensProjectile(World world, EntityPlayer entity, int charge)
	{
		super(world, entity);
		this.charge = charge;
	}

	public EntityLensProjectile(World world, double x, double y, double z, int charge)
	{
		super(world, x, y, z);
		this.charge = charge;
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if (this.getEntityWorld().isRemote)
		{
			return;
		}

		if (ticksExisted > 400 || !this.getEntityWorld().isBlockLoaded(new BlockPos(this)))
		{
			this.setDead();
			return;
		}

		if (this.isInWater())
		{
			this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
			((WorldServer) world).spawnParticle(EnumParticleTypes.SMOKE_LARGE, posX, posY, posZ, 2, 0, 0, 0, 0, new int[0]);
			this.setDead();
		}
	}

	@Override
	protected void apply(RayTraceResult mop)
	{
		if (this.getEntityWorld().isRemote) return;
		WorldHelper.createNovaExplosion(world, getThrower(), posX, posY, posZ, Constants.EXPLOSIVE_LENS_RADIUS[charge]);
	}
	
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		nbt.setInteger("Charge", charge);
	}
	
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		charge = nbt.getInteger("Charge");
	}
}
