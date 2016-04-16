package moze_intel.projecte.gameObjs.container.slots;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public final class SlotPredicates {

    public static final Predicate<ItemStack> HAS_EMC = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack input) {
            return input != null && EMCHelper.doesItemHaveEmc(input);
        }
    };

    public static final Predicate<ItemStack> COLLECTOR_LOCK = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack input) {
            return input != null && FuelMapper.isStackFuel(input);
        }
    };

    public static final Predicate<ItemStack> COLLECTOR_INV = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack input) {
            return input != null && input.getItem() instanceof IItemEmc || (FuelMapper.isStackFuel(input) && !FuelMapper.isStackMaxFuel(input));
        }
    };

    // slotrelayklein, slotmercurialklein
    public static final Predicate<ItemStack> IITEMEMC = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack input) {
            return input != null && input.getItem() instanceof IItemEmc;
        }
    };

    // slotrelayinput
    public static final Predicate<ItemStack> RELAY_INV = Predicates.or(IITEMEMC, HAS_EMC);

    public static final Predicate<ItemStack> MERCURIAL_TARGET = new Predicate<ItemStack>() {
        @Override
        public boolean apply(@Nullable ItemStack input) {
            if (input == null) return false;
            IBlockState state = ItemHelper.stackToState(input);
            return state != null && !(state.getBlock().hasTileEntity(state)) && EMCHelper.doesItemHaveEmc(input);
        }
    };

    private SlotPredicates() {}

}
