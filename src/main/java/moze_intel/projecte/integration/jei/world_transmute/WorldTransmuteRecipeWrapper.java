package moze_intel.projecte.integration.jei.world_transmute;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.recipe.IRecipeWrapper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class WorldTransmuteRecipeWrapper implements IRecipeWrapper {

    private final List<ItemStack> input;
    private final List<ItemStack> output;
    private final List<FluidStack> fluidInput;
    private final List<FluidStack> fluidOutput;

    public WorldTransmuteRecipeWrapper(WorldTransmutations.Entry recipe)
    {
        Block b = recipe.input.getBlock();
        if (FluidRegistry.lookupFluidForBlock(b) != null)
        {
            input = ImmutableList.of();
            fluidInput = ImmutableList.of(new FluidStack(FluidRegistry.lookupFluidForBlock(b), 1000));
        } else
        {
            input = ImmutableList.of(ItemHelper.stateToStack(recipe.input, 1));
            fluidInput = ImmutableList.of();
        }

        ImmutableList.Builder<ItemStack> outStackBuilder = ImmutableList.builder();
        ImmutableList.Builder<FluidStack> outFluidBuilder = ImmutableList.builder();

        for (IBlockState output : new IBlockState[] { recipe.outputs.getLeft(), recipe.outputs.getRight() })
        {
            if (output != null)
            {
                b = output.getBlock();
                if (FluidRegistry.lookupFluidForBlock(b) != null)
                {
                    outFluidBuilder.add(new FluidStack(FluidRegistry.lookupFluidForBlock(b), 1000));
                } else
                {
                    outStackBuilder.add(ItemHelper.stateToStack(output, 1));
                }
            }
        }

        output = outStackBuilder.build();
        fluidOutput = outFluidBuilder.build();
    }

    @Override
    public List<ItemStack> getInputs() {
        return input;
    }

    @Override
    public List<ItemStack> getOutputs() {
        return output;
    }

    @Override
    public List<FluidStack> getFluidInputs() { return fluidInput; }

    @Override
    public List<FluidStack> getFluidOutputs() { return fluidOutput; }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {}

    @Override
    public void drawAnimations(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight) {}

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) { return ImmutableList.of(); }

    @Override
    public boolean handleClick(@Nonnull Minecraft minecraft, int mouseX, int mouseY, int mouseButton) { return false; }
}
