package moze_intel.projecte.api.imc;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

public class CustomConversionRegistration
{
    private final int amount;
    private final Object output;
    private final Map<Object, Integer> input;

    /**
     * Declare to ProjectE's EMC calculation that something is made from something else
     * This can be used to tell ProjectE about recipes it does not know about, or it can be used to "float" the value
     * of something relative to something else.
     *
     * {@code output} and the keys of {@code input} can be the following:
     * <ul>
     *     <li>{@link ItemStack} - Refers to the Item inside.</li>
     *     <li>{@link Block} - Same as {@code Item.getItemFromBlock(block)}.</li>
     *     <li>{@link Item} - Obvious.</li>
     *     <li>{@link FluidStack} - {@link FluidStack#getFluid()} and {@link Fluid#getName()} will be used to identify
     *     this Fluid.</li>
     *     <li>{@link ResourceLocation} - will be interpreted as an Item Tag ID and apply to all items within it.</li>
     *     <li>{@link Object} - (No subclasses of {@code Object} - only {@code Object}!) can be used as a intermediate
     *     fake object for complex conversion.</li>
     * </ul>
     *
     * For example, you can set the value of a specific item in your mod to always be twice the value of gold:
     * <pre>
     *     new CustomConversionRegistration(1, ModItems.myItem, ImmutableMap.of(Items.GOLD_INGOT, 2))
     * </pre>
     *
     * or that the value of another item is equal to the value of the cheapest item in a particular tag:
     * <pre>
     *     new CustomConversionRegistration(1, ModItems.myItemB, ImmutableMap.of(new ResourceLocation("mymod", "mytag"), 1))
     * </pre>
     *
     * @param amount The amount of {@code output} that is produced, millibuckets for fluids
     * @param output The result of this conversion
     * @param input The inputs to the conversion, mapping each ingredient to the amount needed
     */
    public CustomConversionRegistration(int amount, Object output, Map<Object, Integer> input) {
        this.amount = amount;
        this.output = output;
        this.input = input;
        typecheck(this.output);
        for (Object o : input.keySet())
        {
            typecheck(o);
        }
    }

    public int getAmount() {
        return amount;
    }

    public Object getOutput() {
        return output;
    }

    public Map<Object, Integer> getInput() {
        return input;
    }

    private static void typecheck(Object thing)
    {
        if (!(thing instanceof ItemStack || thing instanceof Item || thing instanceof Block || thing instanceof FluidStack
            || thing instanceof ResourceLocation || thing.getClass() == Object.class))
        {
            throw new IllegalArgumentException("Thing does not have a supported type");
        }
    }
}
