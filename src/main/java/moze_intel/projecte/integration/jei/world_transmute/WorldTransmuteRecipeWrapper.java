package moze_intel.projecte.integration.jei.world_transmute;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WorldTransmuteRecipeWrapper implements IRecipeWrapper {

    private final WorldTransmutations.Entry entry;

    public WorldTransmuteRecipeWrapper(WorldTransmutations.Entry recipe)
    {
        this.entry = recipe;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients)
    {
        IBlockState in = entry.input;
        Fluid f = FluidRegistry.lookupFluidForBlock(in.getBlock());
        if (f != null)
        {
            ingredients.setInput(FluidStack.class, new FluidStack(f, Fluid.BUCKET_VOLUME));
        } else
        {
            ingredients.setInput(ItemStack.class, ItemHelper.stateToStack(in, 1));
        }

        IBlockState out = entry.outputs.getLeft();
        IBlockState altOut = entry.outputs.getRight();
        List<ItemStack> outItems = new ArrayList<>(2);
        List<FluidStack> outFluids = new ArrayList<>(2);

        f = FluidRegistry.lookupFluidForBlock(out.getBlock());
        if (f != null)
        {
            outFluids.add(new FluidStack(f, Fluid.BUCKET_VOLUME));
        } else
        {
            outItems.add(ItemHelper.stateToStack(out, 1));
        }

        if (altOut != null)
        {
            f = FluidRegistry.lookupFluidForBlock(altOut.getBlock());
            if (f != null)
            {
                outFluids.add(new FluidStack(f, Fluid.BUCKET_VOLUME));
            } else
            {
                outItems.add(ItemHelper.stateToStack(altOut, 1));
            }
        }

        ingredients.setOutputs(ItemStack.class, outItems);
        ingredients.setOutputs(FluidStack.class, outFluids);
    }

    @Nonnull
    @Override
    public List<ItemStack> getInputs() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public List<ItemStack> getOutputs() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public List<FluidStack> getFluidInputs() { return ImmutableList.of(); }

    @Nonnull
    @Override
    public List<FluidStack> getFluidOutputs() { return ImmutableList.of(); }

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
