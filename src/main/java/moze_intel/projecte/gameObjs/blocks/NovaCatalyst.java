package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class NovaCatalyst extends BlockTNT
{
	public NovaCatalyst()
	{
		this.setTranslationKey("pe_nova_catalyst");
		this.setCreativeTab(ObjHandler.cTab);
	}
	
	@Override
	public void explode(World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityLivingBase entity)
	{
		if (!world.isRemote)
		{
			if (state.getValue(EXPLODE))
			{
				EntityNovaCatalystPrimed catalystPrimed = new EntityNovaCatalystPrimed(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, entity);
				world.spawnEntity(catalystPrimed);
				catalystPrimed.playSound(SoundEvents.ENTITY_TNT_PRIMED, 1, 1);
			}
		}
	}
	
	@Override
	public void onExplosionDestroy(World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion)
	{
		if (!world.isRemote)
		{
			EntityNovaCatalystPrimed catalystPrimed = new EntityNovaCatalystPrimed(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, explosion.getExplosivePlacedBy());
			catalystPrimed.setFuse(world.rand.nextInt(catalystPrimed.getFuse() / 4) + catalystPrimed.getFuse() / 8);
			world.spawnEntity(catalystPrimed);
		}
	}
}
