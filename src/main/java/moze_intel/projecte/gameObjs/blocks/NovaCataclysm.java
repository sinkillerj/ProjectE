package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.entity.EntityNovaCataclysmPrimed;
import net.minecraft.block.TNTBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class NovaCataclysm extends TNTBlock
{
	public NovaCataclysm(Properties props)
	{
		super(props);
	}

	private void explode(World world, BlockPos pos, @Nullable LivingEntity exploder)
	{
		if (!world.isRemote)
		{
			TNTEntity entitytntprimed = new EntityNovaCataclysmPrimed(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, exploder);
			world.addEntity(entitytntprimed);
			world.playSound(null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	// todo 1.14 impossible without https://github.com/MinecraftForge/MinecraftForge/issues/5841
}
