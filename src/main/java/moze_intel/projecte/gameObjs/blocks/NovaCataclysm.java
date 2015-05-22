package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class NovaCataclysm extends NovaCatalyst
{
	public NovaCataclysm()
	{
		this.setUnlocalizedName("pe_nova_cataclysm");
		this.setCreativeTab(ObjHandler.cTab);
	}
	
	@Override
	public void explode(World world, BlockPos pos, IBlockState state, EntityLivingBase entity)
	{
		if (!world.isRemote)
		{
			if (((Boolean)state.getValue(EXPLODE)))
			{
				EntityNovaCataclysmPrimed cataclysmPrimed = new EntityNovaCataclysmPrimed(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, entity);
				world.spawnEntityInWorld(cataclysmPrimed);
				world.playSoundAtEntity(cataclysmPrimed, "game.tnt.primed", 1.0F, 1.0F);
			}
		}
	}

	@Override
	public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosion)
	{
		if (!world.isRemote)
		{
			EntityNovaCataclysmPrimed cataclysmPrimed = new EntityNovaCataclysmPrimed(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, null);
			cataclysmPrimed.fuse = world.rand.nextInt(cataclysmPrimed.fuse / 4) + cataclysmPrimed.fuse / 8;
			world.spawnEntityInWorld(cataclysmPrimed);
		}
	}
}
