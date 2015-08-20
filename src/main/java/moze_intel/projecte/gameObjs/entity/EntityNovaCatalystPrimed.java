package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.world.World;

public class EntityNovaCatalystPrimed extends EntityTNTPrimed
{
	public EntityNovaCatalystPrimed(World world) 
	{
		super(world);
		this.fuse = 20;
	}
	
	public EntityNovaCatalystPrimed(World world, double x, double y, double z, EntityLivingBase placer)
	{
		super(world, x, y, z, placer);
		this.fuse = 20;
	}

	// Need exact override to do our own explosion
	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= 0.03999999910593033D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.onGround)
		{
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
			this.motionY *= -0.5D;
		}

		if (this.fuse-- <= 0)
		{
			this.setDead();

			if (!this.worldObj.isRemote)
				this.explode();
		}
		else
			this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
	}
	
	private void explode()
	{
		WorldHelper.createNovaExplosion(worldObj, this, posX, posY, posZ, 16.0F);
	}
}
