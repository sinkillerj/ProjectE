package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.gameObjs.container.EmcChestBlockEntityContainer;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

// [VanillaCopy] Adapted from ChestBlockEntity
public abstract class EmcChestBlockEntity extends EmcBlockEntity implements LidBlockEntity, MenuProvider {

	private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
		@Override
		protected void onOpen(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
			level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F,
					level.random.nextFloat() * 0.1F + 0.9F);
		}

		@Override
		protected void onClose(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state) {
			level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F,
					level.random.nextFloat() * 0.1F + 0.9F);
		}

		@Override
		protected void openerCountChanged(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, int oldCount, int openCount) {
			level.blockEvent(pos, state.getBlock(), ChestBlock.EVENT_SET_OPEN_COUNT, openCount);
		}

		@Override
		protected boolean isOwnContainer(Player player) {
			return player.containerMenu instanceof EmcChestBlockEntityContainer<?> container && container.blockEntityMatches(EmcChestBlockEntity.this);
		}
	};
	private final ChestLidController chestLidController = new ChestLidController();

	protected EmcChestBlockEntity(BlockEntityTypeRegistryObject<? extends EmcChestBlockEntity> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected EmcChestBlockEntity(BlockEntityTypeRegistryObject<? extends EmcChestBlockEntity> type, BlockPos pos, BlockState state,
			@Range(from = 1, to = Long.MAX_VALUE)long maxAmount) {
		super(type, pos, state, maxAmount);
	}

	public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, EmcChestBlockEntity chest) {
		chest.chestLidController.tickLid();
	}

	@Override
	public boolean triggerEvent(int id, int type) {
		if (id == ChestBlock.EVENT_SET_OPEN_COUNT) {
			this.chestLidController.shouldBeOpen(type > 0);
			return true;
		}
		return super.triggerEvent(id, type);
	}

	public void startOpen(Player player) {
		if (!isRemoved() && !player.isSpectator() && level != null) {
			openersCounter.incrementOpeners(player, level, getBlockPos(), getBlockState());
		}
	}

	public void stopOpen(Player player) {
		if (!isRemoved() && !player.isSpectator() && level != null) {
			openersCounter.decrementOpeners(player, level, getBlockPos(), getBlockState());
		}
	}

	public void recheckOpen() {
		if (!isRemoved() && level != null) {
			openersCounter.recheckOpeners(level, getBlockPos(), getBlockState());
		}
	}

	@Override
	public float getOpenNess(float partialTicks) {
		return chestLidController.getOpenness(partialTicks);
	}
}