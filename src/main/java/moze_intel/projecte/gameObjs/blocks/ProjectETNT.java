package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.TNTBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ProjectETNT extends TNTBlock {

	private final TNTEntityCreator tntEntityCreator;

	public ProjectETNT(Properties properties, TNTEntityCreator tntEntityCreator) {
		super(properties);
		this.tntEntityCreator = tntEntityCreator;
	}

	@Override
	public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
		return 100;
	}

	@Override
	public void catchFire(BlockState state, World world, @Nonnull BlockPos pos, @Nullable Direction side, @Nullable LivingEntity igniter) {
		if (!world.isRemote) {
			TNTEntity tnt = tntEntityCreator.create(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, igniter);
			world.addEntity(tnt);
			world.playSound(null, tnt.posX, tnt.posY, tnt.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}
	}

	@Override
	public void onExplosionDestroy(World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
		if (!world.isRemote) {
			TNTEntity tnt = tntEntityCreator.create(world, (float) pos.getX() + 0.5F, pos.getY(), (float) pos.getZ() + 0.5F, explosion.getExplosivePlacedBy());
			tnt.setFuse((short) (world.rand.nextInt(tnt.getFuse() / 4) + tnt.getFuse() / 8));
			world.addEntity(tnt);
		}
	}

	@FunctionalInterface
	public interface TNTEntityCreator {

		TNTEntity create(World world, double posX, double posY, double posZ, @Nullable LivingEntity igniter);
	}
}