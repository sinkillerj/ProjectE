package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ProjectETNT extends TntBlock {

	private final TNTEntityCreator tntEntityCreator;

	public ProjectETNT(Properties properties, TNTEntityCreator tntEntityCreator) {
		super(properties);
		this.tntEntityCreator = tntEntityCreator;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
		return 100;
	}

	@Override
	public void onCaughtFire(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nullable Direction side, @Nullable LivingEntity igniter) {
		if (!world.isClientSide) {
			createAndAddEntity(world, pos, igniter);
		}
	}

	public void createAndAddEntity(@Nonnull Level world, @Nonnull BlockPos pos, @Nullable LivingEntity igniter) {
		PrimedTnt tnt = tntEntityCreator.create(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, igniter);
		world.addFreshEntity(tnt);
		world.playSound(null, tnt.getX(), tnt.getY(), tnt.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
	}

	public DispenseItemBehavior createDispenseItemBehavior() {
		//Based off vanilla's TNT behavior
		return new DefaultDispenseItemBehavior() {
			@Nonnull
			@Override
			protected ItemStack execute(@Nonnull BlockSource source, @Nonnull ItemStack stack) {
				BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				createAndAddEntity(source.getLevel(), blockpos, null);
				stack.shrink(1);
				return stack;
			}
		};
	}

	@Override
	public void wasExploded(Level world, @Nonnull BlockPos pos, @Nonnull Explosion explosion) {
		if (!world.isClientSide) {
			PrimedTnt tnt = tntEntityCreator.create(world, (float) pos.getX() + 0.5F, pos.getY(), (float) pos.getZ() + 0.5F, explosion.getSourceMob());
			//TODO - 1.18: Re-evaluate this math
			tnt.setFuse((short) (world.random.nextInt(tnt.getFuse() / 4) + tnt.getFuse() / 8));
			world.addFreshEntity(tnt);
		}
	}

	@FunctionalInterface
	public interface TNTEntityCreator {

		PrimedTnt create(Level world, double posX, double posY, double posZ, @Nullable LivingEntity igniter);
	}
}