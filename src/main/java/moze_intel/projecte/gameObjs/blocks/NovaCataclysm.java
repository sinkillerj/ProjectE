package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class NovaCataclysm extends BlockTNT
{
	public NovaCataclysm()
	{
		this.setTranslationKey("pe_nova_cataclysm");
		this.setCreativeTab(ObjHandler.cTab);
	}
	
	@Override
	public void explode(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase entity)
	{
		if (!world.isRemote)
		{
			if (state.getValue(EXPLODE))
			{
				EntityNovaCataclysmPrimed cataclysmPrimed = new EntityNovaCataclysmPrimed(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, entity);
				world.spawnEntity(cataclysmPrimed);
				cataclysmPrimed.playSound(SoundEvents.ENTITY_TNT_PRIMED, 1, 1);
			}
		}
	}

	@Override
	public void onExplosionDestroy(World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion)
	{
		if (!world.isRemote)
		{
			EntityNovaCataclysmPrimed cataclysmPrimed = new EntityNovaCataclysmPrimed(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, explosion.getExplosivePlacedBy());
			cataclysmPrimed.setFuse(world.rand.nextInt(cataclysmPrimed.getFuse() / 4) + cataclysmPrimed.getFuse() / 8);
			world.spawnEntity(cataclysmPrimed);
		}
	}
}
