package moze_intel.projecte.api.imc;

import net.minecraft.world.level.block.state.BlockState;

/**
 * @param origin    defines what will match this transmutation.
 * @param result    defines what the normal right-click result of the transmutation will be.
 * @param altResult defines what the shift right-click result will be, and can be null, in which case it will be set to {@code result}
 */
public record WorldTransmutationEntry(BlockState origin, BlockState result, BlockState altResult) {

	public WorldTransmutationEntry {
		altResult = altResult == null ? result : altResult;
	}
}