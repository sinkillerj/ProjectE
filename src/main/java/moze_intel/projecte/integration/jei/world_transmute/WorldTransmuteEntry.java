package moze_intel.projecte.integration.jei.world_transmute;

import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidBlock;
import org.jetbrains.annotations.Nullable;

public class WorldTransmuteEntry {

	private record StateInfo(ItemStack item, FluidStack fluid) {

		public boolean isEmpty() {
			return item.isEmpty() && fluid.isEmpty();
		}

		public Either<ItemStack, FluidStack> toEither() {
			if (fluid.isEmpty()) {
				return Either.left(item);
			}
			return Either.right(fluid);
		}
	}

	private static final StateInfo EMPTY = new StateInfo(ItemStack.EMPTY, FluidStack.EMPTY);

	private final StateInfo input;
	private final StateInfo leftOutput;
	private final StateInfo rightOutput;

	public WorldTransmuteEntry(WorldTransmutationEntry transmutationEntry) {
		BlockState leftOutputState = transmutationEntry.result();
		BlockState rightOutputState = transmutationEntry.altResult();
		if (leftOutputState == rightOutputState) {
			//Don't show two outputs if it is just a fallback from the primary
			rightOutputState = null;
		}
		input = createInfo(transmutationEntry.origin());
		leftOutput = createInfo(leftOutputState);
		rightOutput = createInfo(rightOutputState);
	}

	private StateInfo createInfo(@Nullable BlockState output) {
		if (output == null) {
			return EMPTY;
		}
		FluidStack outputFluid = fluidFromBlock(output.getBlock());
		if (outputFluid.isEmpty()) {
			return new StateInfo(itemFromBlock(output.getBlock(), output), outputFluid);
		}
		return new StateInfo(ItemStack.EMPTY, outputFluid);
	}

	private FluidStack fluidFromBlock(Block block) {
		if (block instanceof LiquidBlock liquidBlock) {
			return new FluidStack(liquidBlock.getFluid(), FluidType.BUCKET_VOLUME);
		} else if (block instanceof IFluidBlock fluidBlock) {
			return new FluidStack(fluidBlock.getFluid(), FluidType.BUCKET_VOLUME);
		}
		return FluidStack.EMPTY;
	}

	private ItemStack itemFromBlock(Block block, BlockState state) {
		try {
			//We don't have a world or position, but try pick block anyways
			return block.getCloneItemStack(state, null, null, null, null);
		} catch (Exception e) {
			//It failed, probably because of the null world and pos
			return new ItemStack(block);
		}
	}

	public boolean isRenderable() {
		return !input.isEmpty() && (!leftOutput.isEmpty() || !rightOutput.isEmpty());
	}

	public Optional<Either<ItemStack, FluidStack>> getInput() {
		if (input.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(input.toEither());
	}

	public Iterable<Either<ItemStack, FluidStack>> getOutput() {
		List<Either<ItemStack, FluidStack>> outputs = new ArrayList<>();
		if (!leftOutput.isEmpty()) {
			outputs.add(leftOutput.toEither());
		}
		if (!rightOutput.isEmpty()) {
			outputs.add(rightOutput.toEither());
		}
		return outputs;
	}

	public ItemStack getInputItem() {
		return input.item();
	}

	public FluidStack getInputFluid() {
		return input.fluid();
	}
}