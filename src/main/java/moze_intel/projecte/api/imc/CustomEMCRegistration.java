package moze_intel.projecte.api.imc;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CustomEMCRegistration
{
    private final Object thing;
    private final long value;

    /**
     * {@code thing} can be any of the following:
     * <ul>
     *     <li>{@link ItemStack} - Value applies to the item in the stack</li>
     *     <li>{@link ResourceLocation} - will be interpreted as an item tag id and
     *     value applied to all members of the tag.</li>
     *     <li>{@link Object} - (No subclasses of {@code Object} - only {@code Object}!) can be used as a
     *     intermediate fake object for complex recipes.</li>
     * </ul>
     */
    public CustomEMCRegistration(Object thing, long value)
    {
        this.thing = thing;
        this.value = value;
    }

    public Object getThing() {
        return thing;
    }

    public long getValue() {
        return value;
    }
}
