package moze_intel.projecte.integration.crafttweaker.actions;

import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;

import java.util.Iterator;

public abstract class WorldTransmuteAction implements IAction {
    final IBlockState output;
    final IBlockState sneakOutput;
    final IBlockState input;

    private WorldTransmuteAction(IItemStack output, IItemStack sneakOutput, IItemStack input)
    {
        this.output = CraftTweakerMC.getBlock(output).getStateFromMeta(output.getDamage());
        this.sneakOutput = sneakOutput == null ? null : CraftTweakerMC.getBlock(sneakOutput).getStateFromMeta(sneakOutput.getDamage());
        this.input = CraftTweakerMC.getBlock(input).getStateFromMeta(input.getDamage());
    }

    public static class Add extends WorldTransmuteAction
    {
        public Add(IItemStack output, IItemStack sneakOutput, IItemStack input)
        {
            super(output, sneakOutput, input);
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

    public static class Remove extends WorldTransmuteAction
    {
        public Remove(IItemStack output, IItemStack sneakOutput, IItemStack input)
        {
            super(output, sneakOutput, input);
        }

        @Override
        public void apply()
        {
            Iterator<WorldTransmutations.Entry> it = WorldTransmutations.getWorldTransmutations().iterator();
            while (it.hasNext())
            {
                WorldTransmutations.Entry entry = it.next();
                if (entry.input == this.input && entry.outputs.getLeft() == this.output)
                {
                    if (entry.outputs.getRight() == null || entry.outputs.getRight() == this.sneakOutput)
                    {
                        it.remove();
                    }
                }
            }
        }

        @Override
        public String describe()
        {
            return "Removing world transmutation recipe for " + output;
        }
    }

    public static class RemoveAll implements IAction {
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