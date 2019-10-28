package moze_intel.projecte.gameObjs.container.slots;

import java.util.function.Predicate;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;

public final class SlotPredicates {

    public static final Predicate<ItemStack> HAS_EMC = input -> !input.isEmpty() && EMCHelper.doesItemHaveEmc(input);

    public static final Predicate<ItemStack> COLLECTOR_LOCK = input -> !input.isEmpty() && FuelMapper.isStackFuel(input);

    public static final Predicate<ItemStack> COLLECTOR_INV = input -> !input.isEmpty() && input.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).isPresent()
                                                                      || (FuelMapper.isStackFuel(input) && !FuelMapper.isStackMaxFuel(input));

    // slotrelayklein, slotmercurialklein
    public static final Predicate<ItemStack> IITEMEMC = input -> !input.isEmpty() && input.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).isPresent();

    // slotrelayinput
    public static final Predicate<ItemStack> RELAY_INV = input -> IITEMEMC.test(input) || HAS_EMC.test(input);

    public static final Predicate<ItemStack> FURNACE_FUEL = input -> IITEMEMC.test(input) || !input.isEmpty() && AbstractFurnaceTileEntity.isFuel(input);

    public static final Predicate<ItemStack> MERCURIAL_TARGET = input -> {
        if (input.isEmpty()) return false;
        BlockState state = ItemHelper.stackToState(input);
        return state != null && !(state.getBlock().hasTileEntity(state)) && EMCHelper.doesItemHaveEmc(input);
    };

    private SlotPredicates() {}

}
