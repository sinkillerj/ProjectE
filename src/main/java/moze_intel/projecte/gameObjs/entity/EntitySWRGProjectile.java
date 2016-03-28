package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
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
	protected void apply(RayTraceResult mop)
	{
		if(!worldObj.isRemote && mop.typeOfHit == RayTraceResult.Type.BLOCK)
		{
			BlockPos pos = mop.getBlockPos();
			Block up = worldObj.getBlockState(pos.up()).getBlock();
			
			if(worldObj.isAirBlock(pos) || up == Blocks.snow_layer)
			{
				if(tryConsumeEmc(((ItemPE) ObjHandler.arcana), 768))
				{
					EntityLightningBolt lightning = new EntityLightningBolt(worldObj, pos.getX(), pos.getY(), pos.getZ(), false);
					worldObj.addWeatherEffect(lightning);
				}
			}
		}
	}
}
