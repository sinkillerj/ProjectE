package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityLightningProjectile extends EntityThrowable
{
	private final ItemStack ring;
	
	public EntityLightningProjectile(World world)
	{
		super(world);
		
		this.ring = null;
	}
	
	public EntityLightningProjectile(World world, EntityLivingBase player, ItemStack ring)
	{
		super(world, player);
		
		this.ring = ring;
	}
	
	protected void onImpact(MovingObjectPosition mop)
	{
		if(!worldObj.isRemote && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			int x = mop.blockX;
			int y = mop.blockY;
			int z = mop.blockZ;
			
			Block up = worldObj.getBlock(x, y + 1, z);
			
			if(up == Blocks.air || up == Blocks.snow_layer)
			{
				EntityPlayer player = (EntityPlayer)getThrower();
				if(ItemPE.consumeFuel(player, ring, 768, true))
				{
					EntityLightningBolt lightning = new EntityLightningBolt(worldObj, x, y, z);
					worldObj.addWeatherEffect(lightning);
				}
			}
			
			setDead();
		}
	}
}
