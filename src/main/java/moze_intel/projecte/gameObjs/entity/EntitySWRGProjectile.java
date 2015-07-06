package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySWRGProjectile extends PEProjectile
{
	public EntitySWRGProjectile(World world)
	{
		super(world);
	}
	
	public EntitySWRGProjectile(World world, EntityPlayer player)
	{
		super(world, player);
	}
	
	@Override
	protected void apply(MovingObjectPosition mop)
	{
		if(!worldObj.isRemote && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
		{
			int x = mop.blockX;
			int y = mop.blockY;
			int z = mop.blockZ;
			
			Block up = worldObj.getBlock(x, y + 1, z);
			
			if(up == Blocks.air || up == Blocks.snow_layer)
			{
				if(tryConsumeEmc(((ItemPE) ObjHandler.arcana), 768))
				{
					EntityLightningBolt lightning = new EntityLightningBolt(worldObj, x, y, z);
					worldObj.addWeatherEffect(lightning);
				}
			}
		}
	}
}
