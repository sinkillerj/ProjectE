package moze_intel.projecte.integration.jei.world_transmute;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldTransmuteEntry implements IRecipeWrapper
{
	private ItemStack inputItem = ItemStack.EMPTY;
	private ItemStack leftOutputItem = ItemStack.EMPTY;
	private ItemStack rightOutputItem = ItemStack.EMPTY;
	private FluidStack inputFluid;
	private FluidStack leftOutputFluid;
	private FluidStack rightOutputFluid;

	public WorldTransmuteEntry(WorldTransmutations.Entry transmutationEntry)
	{
		Block inputBlock = transmutationEntry.input.getBlock();
		IBlockState leftOutput = transmutationEntry.outputs.getLeft();
		IBlockState rightOutput = transmutationEntry.outputs.getRight();

		inputFluid = fluidFromBlock(inputBlock);
		if (inputFluid == null)
		{
			inputItem = itemFromBlock(inputBlock, transmutationEntry.input);
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
			return new FluidStack(FluidRegistry.WATER, 1000);
		}
		else if (block == Blocks.LAVA)
		{
			return new FluidStack(FluidRegistry.LAVA, 1000);
		}
		return null;
	}

	private ItemStack itemFromBlock(Block block, IBlockState state)
	{
		try
		{
			//We don't have a world or position, but try pick block anyways
			return block.getPickBlock(state, null, null, null, null);
		} catch (Exception e)
		{
			//It failed, probably because of the null world and pos
			return ItemHelper.stateToStack(state, 1);
		}
	}

	public boolean isRenderable()
	{
		boolean hasInput = inputFluid != null || !inputItem.isEmpty();
		boolean hasLeftOutput = leftOutputFluid != null || !leftOutputItem.isEmpty();
		boolean hasRightOutput = rightOutputFluid != null || !rightOutputItem.isEmpty();
		return hasInput && (hasLeftOutput || hasRightOutput);
	}

	@Override
	public void getIngredients(@Nonnull IIngredients ingredients)
	{
		if (inputFluid != null)
		{
			ingredients.setInput(FluidStack.class, inputFluid);
		}
		else if (!inputItem.isEmpty())
		{
			ingredients.setInput(ItemStack.class, inputItem);
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
			ingredients.setOutputs(FluidStack.class, fluidOutputs);
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
			ingredients.setOutputs(ItemStack.class, outputList);
		}
	}

	@Override
	@Nonnull
	public java.util.List<String> getTooltipStrings(int mouseX, int mouseY)
	{
		if (mouseX > 67 && mouseX < 107 && mouseY > 18 && mouseY < 38)
		{
			return Collections.singletonList(I18n.format("pe.nei.worldtransmute.description"));
		}
		return Collections.emptyList();
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