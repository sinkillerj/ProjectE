package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityMobRandomizer extends PEProjectile
{
	public EntityMobRandomizer(World world) 
	{
		super(world);
	}
	
	public EntityMobRandomizer(World world, EntityPlayer entity) 
	{
		super(world, entity);
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if (!this.worldObj.isRemote)
		{
			if (ticksExisted > 400 || this.isInWater() || !this.worldObj.isBlockLoaded(new BlockPos(this)))
			{
				this.setDead();
			}
		}
	}

	@Override
	protected void apply(RayTraceResult mop)
	{
		if (!this.worldObj.isRemote)
		{
			if (this.isInWater())
			{
				this.setDead();
				return;
			}
		}

		if (this.worldObj.isRemote)
		{
			for (int i = 0; i < 4; ++i)
			{
				this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
			}
			return;
		}

		if (!(mop.entityHit instanceof EntityLiving))
		{
			return;
		}

		EntityLiving ent = ((EntityLiving) mop.entityHit);
		Entity randomized = WorldHelper.getRandomEntity(this.worldObj, ent);
		
		if (randomized != null && EMCHelper.consumePlayerFuel(((EntityPlayer) getThrower()), 384) != -1)
		{
			ent.setDead();
			randomized.setLocationAndAngles(ent.posX, ent.posY, ent.posZ, ent.rotationYaw, ent.rotationPitch);
			this.worldObj.spawnEntityInWorld(randomized);
		}
	}
}
