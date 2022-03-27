package moze_intel.projecte.api.imc;

import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public record WorldTransmutationEntry(BlockState origin, BlockState result, @Nullable BlockState altResult) {

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
}