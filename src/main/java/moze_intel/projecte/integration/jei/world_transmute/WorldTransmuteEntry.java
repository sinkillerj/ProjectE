/*
package moze_intel.projecte.integration.jei.world_transmute;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import moze_intel.projecte.api.imc.WorldTransmutationEntry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WorldTransmuteEntry
{
	private ItemStack inputItem = ItemStack.EMPTY;
	private ItemStack leftOutputItem = ItemStack.EMPTY;
	private ItemStack rightOutputItem = ItemStack.EMPTY;
	private FluidStack inputFluid;
	private FluidStack leftOutputFluid;
	private FluidStack rightOutputFluid;

	public WorldTransmuteEntry(WorldTransmutationEntry transmutationEntry)
	{
		Block inputBlock = transmutationEntry.getOrigin().getBlock();
		BlockState leftOutput = transmutationEntry.getResult();
		BlockState rightOutput = transmutationEntry.getAltResult();

		inputFluid = fluidFromBlock(inputBlock);
		if (inputFluid == null)
		{
			inputItem = itemFromBlock(inputBlock, transmutationEntry.getOrigin());
		}
		if (leftOutput != null)
		{
			leftOutputFluid = fluidFromBlock(leftOutput.getBlock());
			if (leftOutputFluid == null)
			{
				leftOutputItem = itemFromBlock(leftOutput.getBlock(), leftOutput);
			}
		}
		if (rightOutput != null)
		{
			rightOutputFluid = fluidFromBlock(rightOutput.getBlock());
			if (rightOutputFluid == null)
			{
				rightOutputItem = itemFromBlock(rightOutput.getBlock(), rightOutput);
			}
		}
	}

	private FluidStack fluidFromBlock(Block block)
	{
		if (block == Blocks.WATER)
		{
			// return new FluidStack(FluidRegistry.WATER, 1000);
		}
		else if (block == Blocks.LAVA)
		{
			// return new FluidStack(FluidRegistry.LAVA, 1000);
		}
		return null;
	}

	private ItemStack itemFromBlock(Block block, BlockState state)
	{
		try
		{
			//We don't have a world or position, but try pick block anyways
			return block.getPickBlock(state, null, null, null, null);
		} catch (Exception e)
		{
			//It failed, probably because of the null world and pos
			return new ItemStack(block);
		}
	}

	public boolean isRenderable()
	{
		boolean hasInput = inputFluid != null || !inputItem.isEmpty();
		boolean hasLeftOutput = leftOutputFluid != null || !leftOutputItem.isEmpty();
		boolean hasRightOutput = rightOutputFluid != null || !rightOutputItem.isEmpty();
		return hasInput && (hasLeftOutput || hasRightOutput);
	}

	public void setIngredients(@Nonnull IIngredients ingredients)
	{
		if (inputFluid != null)
		{
			ingredients.setInput(VanillaTypes.FLUID, inputFluid);
		}
		else if (!inputItem.isEmpty())
		{
			ingredients.setInput(VanillaTypes.ITEM, inputItem);
		}

		List<FluidStack> fluidOutputs = new ArrayList<>();
		if (leftOutputFluid != null)
		{
			fluidOutputs.add(leftOutputFluid);
		}
		if (rightOutputFluid != null)
		{
			fluidOutputs.add(rightOutputFluid);
		}
		if (!fluidOutputs.isEmpty())
		{
			ingredients.setOutputs(VanillaTypes.FLUID, fluidOutputs);
		}

		List<ItemStack> outputList = new ArrayList<>();
		if (!leftOutputItem.isEmpty())
		{
			outputList.add(leftOutputItem);
		}
		if (!rightOutputItem.isEmpty())
		{
			outputList.add(rightOutputItem);
		}
		if (!outputList.isEmpty())
		{
			ingredients.setOutputs(VanillaTypes.ITEM, outputList);
		}
	}

	public ItemStack getInputItem()
	{
		return inputItem;
	}

	public FluidStack getInputFluid()
	{
		return inputFluid;
	}
}
*/
