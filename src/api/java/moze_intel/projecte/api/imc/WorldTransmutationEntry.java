package moze_intel.projecte.api.imc;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;

public class WorldTransmutationEntry {

	private final BlockState origin;
	private final BlockState result;
	private final BlockState altResult;

	/**
	 * @param origin    defines what will match this transmutation.
	 * @param result    defines what the normal right-click result of the transmutation will be.
	 * @param altResult defines what the shift right-click result will be, and can be null, in which {@code result} will be used instead
	 */
	public WorldTransmutationEntry(BlockState origin, BlockState result, @Nullable BlockState altResult) {
		this.origin = origin;
		this.result = result;
		this.altResult = altResult == null ? this.result : altResult;
	}

	public BlockState getOrigin() {
		return origin;
	}

	public BlockState getResult() {
		return result;
	}

	public BlockState getAltResult() {
		return altResult;
	}
}