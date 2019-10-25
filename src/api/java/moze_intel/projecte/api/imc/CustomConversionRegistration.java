package moze_intel.projecte.api.imc;

import java.util.Map;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;

public class CustomConversionRegistration
{
    private final int amount;
    private final NormalizedSimpleStack output;
    private final Map<NormalizedSimpleStack, Integer> input;

    /**
     * Declare to ProjectE's EMC calculation that something is made from something else
     * This can be used to tell ProjectE about recipes it does not know about, or it can be used to "float" the value
     * of something relative to something else.
     *
     * For example, you can set the value of a specific item in your mod to always be twice the value of gold:
     * <pre>
     *     new CustomConversionRegistration(1, NSSItem.createItem(ModItems.myItem), ImmutableMap.of(NSSItem.createItem(Items.GOLD_INGOT), 2))
     * </pre>
     *
     * or that the value of another item is equal to the value of the cheapest item in a particular tag:
     * <pre>
     *     new CustomConversionRegistration(1, NSSItem.createItem(ModItems.myItemB), ImmutableMap.of(NSSItem.createTag(new ResourceLocation("mymod", "mytag")), 1))
     * </pre>
     *
     * @param amount The amount of {@code output} that is produced, millibuckets for fluids
     * @param output The result of this conversion
     * @param input The inputs to the conversion, mapping each ingredient to the amount needed
     */
    public CustomConversionRegistration(int amount, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> input) {
        this.amount = amount;
        this.output = output;
        this.input = input;
    }

    public int getAmount() {
        return amount;
    }

    public NormalizedSimpleStack getOutput() {
        return output;
    }

    public Map<NormalizedSimpleStack, Integer> getInput() {
        return input;
    }
}
