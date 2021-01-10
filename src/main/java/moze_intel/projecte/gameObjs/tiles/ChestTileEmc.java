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
		if (++ticksSinceSync % 20 * 4 == 0) {
			world.addBlockEvent(pos, getBlockState().getBlock(), 1, numPlayersUsing);
		}

		prevLidAngle = lidAngle;
		if (numPlayersUsing > 0 && lidAngle == 0.0F) {
			world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
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
				world.playSound(null, pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
			}
			if (lidAngle < 0.0F) {
				lidAngle = 0.0F;
			}
		}
	}

	@Override
	public boolean receiveClientEvent(int number, int arg) {
		if (number == 1) {
			numPlayersUsing = arg;
			return true;
		}
		return super.receiveClientEvent(number, arg);
	}

	public float getLidAngle(float partialTicks) {
		//Only used on the client
		return MathHelper.lerp(partialTicks, this.prevLidAngle, this.lidAngle);
	}
}