package moze_intel.projecte.integration.jei.world_transmute;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class WorldTransmuteEntry {

	private record StateInfo(ItemStack item, FluidStack fluid) {

		public boolean isEmpty() {
			return item.isEmpty() && fluid.isEmpty();
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
			return new FluidStack(liquidBlock.getFluid(), FluidAttributes.BUCKET_VOLUME);
		} else if (block instanceof IFluidBlock fluidBlock) {
			return new FluidStack(fluidBlock.getFluid(), FluidAttributes.BUCKET_VOLUME);
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

	public void setIngredients(@Nonnull IIngredients ingredients) {
		if (!input.fluid().isEmpty()) {
			ingredients.setInput(VanillaTypes.FLUID, input.fluid());
		} else if (!input.item().isEmpty()) {
			ingredients.setInput(VanillaTypes.ITEM, input.item());
		}

		List<FluidStack> fluidOutputs = new ArrayList<>();
		if (!leftOutput.fluid().isEmpty()) {
			fluidOutputs.add(leftOutput.fluid());
		}
		if (!rightOutput.fluid().isEmpty()) {
			fluidOutputs.add(rightOutput.fluid());
		}
		if (!fluidOutputs.isEmpty()) {
			ingredients.setOutputs(VanillaTypes.FLUID, fluidOutputs);
		}

		List<ItemStack> outputList = new ArrayList<>();
		if (!leftOutput.item().isEmpty()) {
			outputList.add(leftOutput.item());
		}
		if (!rightOutput.item().isEmpty()) {
			outputList.add(rightOutput.item());
		}
		if (!outputList.isEmpty()) {
			ingredients.setOutputs(VanillaTypes.ITEM, outputList);
		}
	}

	public ItemStack getInputItem() {
		return input.item();
	}

	public FluidStack getInputFluid() {
		return input.fluid();
	}
}