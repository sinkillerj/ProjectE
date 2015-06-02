package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
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
	
	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		if(!worldObj.isRemote && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			BlockPos pos = mop.getBlockPos();
			Block up = worldObj.getBlockState(pos.up()).getBlock();
			
			if(worldObj.isAirBlock(pos) || up == Blocks.snow_layer)
			{
				EntityPlayer player = (EntityPlayer)getThrower();
				if(ItemPE.consumeFuel(player, ring, 768, true))
				{
					EntityLightningBolt lightning = new EntityLightningBolt(worldObj, pos.getX(), pos.getY(), pos.getZ());
					worldObj.addWeatherEffect(lightning);
				}
			}
			
			setDead();
		}
	}
}
