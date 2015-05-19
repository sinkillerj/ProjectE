package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class NovaCatalyst extends BlockTNT
{
	public NovaCatalyst()
	{
		this.setUnlocalizedName("pe_nova_catalyst");
		this.setCreativeTab(ObjHandler.cTab);
	}
	
	@Override
	public void func_180692_a(World world, BlockPos pos, IBlockState state, EntityLivingBase entity)
	{
		if (world.isRemote || par5 != 1)
		{
			return;
		}
		
		if (entity == null)
		{
			entity = world.getClosestPlayer(x, y, z, 64);
		}

		EntityNovaCatalystPrimed ent = new EntityNovaCatalystPrimed(world, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), entity); 
		world.spawnEntityInWorld(ent);
		world.playSoundAtEntity(ent, "game.tnt.primed", 1.0F, 1.0F);
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
	{
		func_180692_a(world, x, y, z, 1, null);
	}
}
