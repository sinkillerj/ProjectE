package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Particles;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityMobRandomizer extends EntityThrowable
{
	public EntityMobRandomizer(World world) 
	{
		super(ObjHandler.MOB_RANDOMIZER, world);
	}
	
	public EntityMobRandomizer(EntityPlayer entity, World world)
	{
		super(ObjHandler.MOB_RANDOMIZER, entity, world);
	}
	
	@Override
	public void tick()
	{
		super.tick();
		
		if (!this.getEntityWorld().isRemote)
		{
			if (ticksExisted > 400 || this.isInWater() || !this.getEntityWorld().isBlockLoaded(new BlockPos(this)))
			{
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
		if (this.getEntityWorld().isRemote)
		{
			for (int i = 0; i < 4; ++i)
			{
				this.getEntityWorld().addParticle(Particles.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
			}
			return;
		}

		if (this.isInWater() || !(mop.entity instanceof EntityLiving) || !(getThrower() instanceof EntityPlayer))
		{
			remove();
			return;
		}

		EntityLiving ent = ((EntityLiving) mop.entity);
		EntityLiving randomized = WorldHelper.getRandomEntity(this.getEntityWorld(), ent);
		
		if (randomized != null && EMCHelper.consumePlayerFuel(((EntityPlayer) getThrower()), 384) != -1)
		{
			ent.remove();
			randomized.setLocationAndAngles(ent.posX, ent.posY, ent.posZ, ent.rotationYaw, ent.rotationPitch);
			randomized.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(randomized)), null, null);
			this.getEntityWorld().spawnEntity(randomized);
			randomized.spawnExplosionParticle();
		}
		remove();
	}
}
