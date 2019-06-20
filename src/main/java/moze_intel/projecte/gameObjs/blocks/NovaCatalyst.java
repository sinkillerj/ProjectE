package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.entity.EntityNovaCatalystPrimed;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NovaCatalyst extends TNTBlock
{
	public NovaCatalyst(Properties props)
	{
		super(props);
	}

	private void explode(World world, BlockPos pos, @Nullable LivingEntity exploder)
	{
		if (!world.isRemote)
		{
			TNTEntity entitytntprimed = new EntityNovaCatalystPrimed(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, exploder);
			world.addEntity(entitytntprimed);
			world.playSound(null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	// todo 1.14 impossible without https://github.com/MinecraftForge/MinecraftForge/issues/5841
}
