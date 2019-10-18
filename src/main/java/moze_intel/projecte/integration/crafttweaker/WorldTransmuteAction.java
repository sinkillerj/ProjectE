package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.actions.IUndoableAction;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.BlockState;

abstract class WorldTransmuteAction implements IUndoableAction {

    protected final BlockState input;
    protected final BlockState output;
    protected final BlockState sneakOutput;

    private WorldTransmuteAction(MCBlockState input, MCBlockState output, MCBlockState sneakOutput)
    {
        this(input.getInternal(), output.getInternal(), sneakOutput.getInternal());
    }

    private WorldTransmuteAction(BlockState input, BlockState output, BlockState sneakOutput)
    {
        this.input = input;
        this.output = output;
        this.sneakOutput = sneakOutput;
    }

    protected void apply(boolean add)
    {
        if (add) {
            WorldTransmutations.register(this.input, this.output, this.sneakOutput);
        } else {
            WorldTransmutations.getWorldTransmutations().removeIf(entry -> entry.getOrigin() == this.input &&
                    entry.getResult() == this.output && entry.getAltResult() == this.sneakOutput);
        }
    }

    static class Add extends WorldTransmuteAction
    {
        Add(MCBlockState input, MCBlockState output, MCBlockState sneakOutput)
        {
            super(input, output, sneakOutput);
        }

        @Override
        public void apply()
        {
            apply(true);
        }

        @Override
        public String describe()
        {
            if (sneakOutput == null) {
                return "Adding world transmutation recipe for: " + input + " with output: " + output;
            }
            return "Adding world transmutation recipe for: " + input + " with output: " + output + " and secondary output: " + sneakOutput;
        }

        @Override
        public void undo() {
            apply(false);
        }

        @Override
        public String describeUndo() {
            if (sneakOutput == null) {
                return "Undoing addition of world transmutation recipe for: " + input + " with output: " + output;
            }
            return "Undoing addition of world transmutation recipe for: " + input + " with output: " + output + " and secondary output: " + sneakOutput;
        }
    }

    static class Remove extends WorldTransmuteAction
    {
        Remove(MCBlockState input, MCBlockState output, MCBlockState sneakOutput)
        {
            super(input, output, sneakOutput);
        }

        @Override
        public void apply()
        {
            apply(false);
        }

        @Override
        public String describe()
        {
            if (sneakOutput == null) {
                return "Removing world transmutation recipe for: " + input + " with output: " + output;
            }
            return "Removing world transmutation recipe for: " + input + " with output: " + output + " and secondary output: " + sneakOutput;
        }

        @Override
        public void undo() {
            apply(true);
        }

        @Override
        public String describeUndo() {
            if (sneakOutput == null) {
                return "Undoing removal of world transmutation recipe for: " + input + " with output: " + output;
            }
            return "Undoing removal of world transmutation recipe for: " + input + " with output: " + output + " and secondary output: " + sneakOutput;
        }
    }

    static class RemoveAll implements IUndoableAction {
        @Override
        public void apply() {
            WorldTransmutations.getWorldTransmutations().clear();
        }

        @Override
        public String describe() {
            return "Removing all world transmutation recipes";
        }

        @Override
        public void undo() {
            WorldTransmutations.resetWorldTransmutations();
        }

        @Override
        public String describeUndo() {
            return "Restored world transmutation recipes to default";
        }
    }
}