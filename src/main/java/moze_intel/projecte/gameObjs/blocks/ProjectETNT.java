package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.TNTBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.item.ItemStack;
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
	public void catchFire(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nullable Direction side, @Nullable LivingEntity igniter) {
		if (!world.isClientSide) {
			createAndAddEntity(world, pos, igniter);
		}
	}

	public void createAndAddEntity(@Nonnull World world, @Nonnull BlockPos pos, @Nullable LivingEntity igniter) {
		TNTEntity tnt = tntEntityCreator.create(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, igniter);
		world.addFreshEntity(tnt);
		world.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	public IDispenseItemBehavior createDispenseItemBehavior() {
		//Based off vanilla's TNT behavior
		return new DefaultDispenseItemBehavior() {
			@Nonnull
			@Override
			protected ItemStack execute(@Nonnull IBlockSource source, @Nonnull ItemStack stack) {
				BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				createAndAddEntity(source.getLevel(), blockpos, null);
				stack.shrink(1);
				return stack;
			}
		};
	}

	@Override
	public void wasExploded(World world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
		if (!world.isClientSide) {
			TNTEntity tnt = tntEntityCreator.create(world, (float) pos.getX() + 0.5F, pos.getY(), (float) pos.getZ() + 0.5F, explosion.getSourceMob());
			tnt.setFuse((short) (world.random.nextInt(tnt.getLife() / 4) + tnt.getLife() / 8));
			world.addFreshEntity(tnt);
		}
	}

	@FunctionalInterface
	public interface TNTEntityCreator {

		TNTEntity create(World world, double posX, double posY, double posZ, @Nullable LivingEntity igniter);
	}
}