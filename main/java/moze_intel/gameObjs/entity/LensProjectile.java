package moze_intel.gameObjs.entity;

import moze_intel.utils.Constants;
import moze_intel.utils.NovaExplosion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class LensProjectile extends EntityThrowable
{
	private byte charge;
	
	public LensProjectile(World world) 
	{
		super(world);
	}

	public LensProjectile(World world, EntityLivingBase entity, byte charge) 
	{
		super(world, entity);
		this.charge = charge;
	}

	public LensProjectile(World world, double x, double y, double z, byte charge) 
	{
		super(world, x, y, z);
		this.charge = charge;
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if (this.worldObj.isRemote) 
			return;
		
		if (this.isInWater())
			this.setDead();
	}
	
	@Override
	protected float getGravityVelocity()
	{	
		return 0;
	}

	@Override
	protected void onImpact(MovingObjectPosition mop) 
	{
		if (this.worldObj.isRemote) return;
		NovaExplosion explosion = new NovaExplosion(this.worldObj, this.getThrower(), this.posX, this.posY, this.posZ, Constants.explosiveLensRadius[charge]);
		explosion.doExplosionA();
		explosion.doExplosionB(true);
		this.setDead();
	}
	
	public void writeEntityToNBT(NBTTagCompound nbt)
    {
		super.writeEntityToNBT(nbt);
		nbt.setByte("Charge", charge);
    }
	
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
    	super.readEntityFromNBT(nbt);
    	charge = nbt.getByte("Charge");
    }
}
