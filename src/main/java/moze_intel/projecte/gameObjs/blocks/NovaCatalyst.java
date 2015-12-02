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
	public void explode(World world, BlockPos pos, IBlockState state, EntityLivingBase entity)
	{
		if (!world.isRemote)
		{
			if (state.getValue(EXPLODE))
			{
				EntityNovaCatalystPrimed catalystPrimed = new EntityNovaCatalystPrimed(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, entity);
				world.spawnEntityInWorld(catalystPrimed);
				world.playSoundAtEntity(catalystPrimed, "game.tnt.primed", 1.0F, 1.0F);
			}
		}
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
	{
		if (!world.isRemote)
		{
			EntityNovaCatalystPrimed catalystPrimed = new EntityNovaCatalystPrimed(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, explosion.getExplosivePlacedBy());
			catalystPrimed.fuse = world.rand.nextInt(catalystPrimed.fuse / 4) + catalystPrimed.fuse / 8;
			world.spawnEntityInWorld(catalystPrimed);
		}
	}
}
