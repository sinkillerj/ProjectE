package moze_intel.projecte.gameObjs.block_entities;

import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ChestTileEmc extends CapabilityTileEMC {

	private int ticksSinceSync;
	private float lidAngle;
	private float prevLidAngle;
	public int numPlayersUsing;

	protected ChestTileEmc(BlockEntityTypeRegistryObject<? extends ChestTileEmc> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	protected void updateChest() {
		if (level == null) {
			return;
		}
		if (++ticksSinceSync % 20 * 4 == 0) {
			level.blockEvent(worldPosition, getBlockState().getBlock(), 1, numPlayersUsing);
		}

		prevLidAngle = lidAngle;
		if (numPlayersUsing > 0 && lidAngle == 0.0F) {
			level.playSound(null, worldPosition, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
		}

		if (numPlayersUsing == 0 && lidAngle > 0.0F || numPlayersUsing > 0 && lidAngle < 1.0F) {
			float angleIncrement = 0.1F;
			if (numPlayersUsing > 0) {
				lidAngle += angleIncrement;
			} else {
				lidAngle -= angleIncrement;
			}
			if (lidAngle > 1.0F) {
				lidAngle = 1.0F;
			}
			if (lidAngle < 0.5F && prevLidAngle >= 0.5F) {
				level.playSound(null, worldPosition, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
			}
			if (lidAngle < 0.0F) {
				lidAngle = 0.0F;
			}
		}
	}

	@Override
	public boolean triggerEvent(int number, int arg) {
		if (number == 1) {
			numPlayersUsing = arg;
			return true;
		}
		return super.triggerEvent(number, arg);
	}

	public float getLidAngle(float partialTicks) {
		//Only used on the client
		return Mth.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
	}
}