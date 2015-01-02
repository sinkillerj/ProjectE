package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ParticlePKT;
import moze_intel.projecte.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;

public class EntityMobRandomizer extends EntityThrowable
{
	private EntityPlayer shooter;
	
	public EntityMobRandomizer(World world) 
	{
		super(world);
	}
	
	public EntityMobRandomizer(World world, EntityPlayer entity) 
	{
		super(world, entity);
		shooter = entity;
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if (!this.worldObj.isRemote)
		{
			if (this.isInWater() || shooter == null)
			{
				this.setDead();
			}
		}
	}
	
	@Override
	protected float getGravityVelocity()
	{
		return 0.0F;
	}

	@Override
	protected void onImpact(MovingObjectPosition mop) 
	{
		if (this.worldObj.isRemote || mop.typeOfHit != MovingObjectType.ENTITY)
		{
			return;
		}
		
		Entity ent = mop.entityHit;
		Entity randomized = Utils.getRandomEntity(this.worldObj, ent);
		
		if (ent instanceof EntityLiving && randomized != null && Utils.consumePlayerFuel(shooter, 384) != -1)
		{
			this.setDead();
			ent.setDead();
			randomized.setLocationAndAngles(ent.posX, ent.posY, ent.posZ, ent.rotationYaw, ent.rotationPitch);
			this.worldObj.spawnEntityInWorld(randomized);
			
			for (int i = 0; i < 4; i++)
			{
				PacketHandler.sendToAllAround(new ParticlePKT("portal", ent.posX + (this.rand.nextDouble() - 0.5D) * (double)ent.width, ent.posY + this.rand.nextDouble() * (double)ent.height - 0.25D, ent.posZ + (this.rand.nextDouble() - 0.5D) * (double)ent.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D),
				new TargetPoint(this.worldObj.provider.dimensionId, ent.posX, ent.posY, ent.posZ, 32));
			}
		}
	}
}
