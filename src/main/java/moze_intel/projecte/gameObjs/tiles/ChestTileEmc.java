package moze_intel.projecte.gameObjs.tiles;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

public abstract class ChestTileEmc extends CapabilityTileEMC {

	private int ticksSinceSync;
	private float lidAngle;
	private float prevLidAngle;
	public int numPlayersUsing;

	protected ChestTileEmc(TileEntityType<?> type) {
		super(type);
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
			level.playSound(null, worldPosition, SoundEvents.CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
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
				level.playSound(null, worldPosition, SoundEvents.CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
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
		return MathHelper.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
	}
}