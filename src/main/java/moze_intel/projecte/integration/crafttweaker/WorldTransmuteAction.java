package moze_intel.projecte.integration.crafttweaker;

import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;

abstract class WorldTransmuteAction implements IAction {
    final IBlockState output;
    final IBlockState sneakOutput;
    final IBlockState input;

    private WorldTransmuteAction(IItemStack output, IItemStack input, IItemStack sneakOutput)
    {
        this(CraftTweakerMC.getBlock(output).getStateFromMeta(output.getDamage()),
                CraftTweakerMC.getBlock(input).getStateFromMeta(input.getDamage()),
                sneakOutput == null ? null : CraftTweakerMC.getBlock(sneakOutput).getStateFromMeta(sneakOutput.getDamage()));
    }

    private WorldTransmuteAction(crafttweaker.api.block.IBlockState output, crafttweaker.api.block.IBlockState input, crafttweaker.api.block.IBlockState sneakOutput)
    {
        this(CraftTweakerMC.getBlockState(output), CraftTweakerMC.getBlockState(input), CraftTweakerMC.getBlockState(sneakOutput));
    }

    private WorldTransmuteAction(IBlockState output, IBlockState input, IBlockState sneakOutput)
    {
        this.output = output;
        this.sneakOutput = sneakOutput;
        this.input = input;
    }

    static class Add extends WorldTransmuteAction
    {
        Add(IItemStack output, IItemStack input, IItemStack sneakOutput)
        {
            super(output, input, sneakOutput);
        }

        Add(crafttweaker.api.block.IBlockState output, crafttweaker.api.block.IBlockState input, crafttweaker.api.block.IBlockState sneakOutput)
        {
            super(output, input, sneakOutput);
        }

        @Override
        public void apply()
        {
            WorldTransmutations.register(this.input, this.output, this.sneakOutput);
        }

        @Override
        public String describe()
        {
            return "Adding world transmutation recipe for " + output;
        }
    }

    static class Remove extends WorldTransmuteAction
    {
        Remove(IItemStack output, IItemStack input, IItemStack sneakOutput)
        {
            super(output, input, sneakOutput);
        }

        Remove(crafttweaker.api.block.IBlockState output, crafttweaker.api.block.IBlockState input, crafttweaker.api.block.IBlockState sneakOutput)
        {
            super(output, input, sneakOutput);
        }

        @Override
        public void apply()
        {
            WorldTransmutations.getWorldTransmutations().removeIf(entry -> entry.input == this.input && entry.outputs.getLeft() == this.output && entry.outputs.getRight() == this.sneakOutput);
        }

        @Override
        public String describe()
        {
            return "Removing world transmutation recipe for " + output;
        }
    }

    static class RemoveAll implements IAction {
        @Override
        public void apply() {
            WorldTransmutations.getWorldTransmutations().clear();
        }

        @Override
        public String describe() {
            return "Removing all world transmutation recipes";
        }
    }
}