package moze_intel.projecte.gameObjs.container.slots;

import com.google.common.base.Predicate;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.item.ItemStack;

public final class SlotPredicates {

    public static final Predicate<ItemStack> HAS_EMC = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack input) {
            return input != null && EMCHelper.doesItemHaveEmc(input);
        }
    };

    private SlotPredicates() {}

}
