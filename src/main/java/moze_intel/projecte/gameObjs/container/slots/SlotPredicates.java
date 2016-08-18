package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.function.Predicate;

public final class SlotPredicates {

    public static final Predicate<ItemStack> HAS_EMC = input -> input != null && EMCHelper.doesItemHaveEmc(input);

    public static final Predicate<ItemStack> COLLECTOR_LOCK = input -> input != null && FuelMapper.isStackFuel(input);

    public static final Predicate<ItemStack> COLLECTOR_INV = input -> input != null && input.getItem() instanceof IItemEmc || (FuelMapper.isStackFuel(input) && !FuelMapper.isStackMaxFuel(input));

    // slotrelayklein, slotmercurialklein
    public static final Predicate<ItemStack> IITEMEMC = input -> input != null && input.getItem() instanceof IItemEmc;

    // slotrelayinput
    public static final Predicate<ItemStack> RELAY_INV = input -> IITEMEMC.test(input) || HAS_EMC.test(input);

    public static final Predicate<ItemStack> FURNACE_FUEL = input -> IITEMEMC.test(input) || input != null && TileEntityFurnace.isItemFuel(input);

    public static final Predicate<ItemStack> SMELTABLE = input -> input != null && FurnaceRecipes.instance().getSmeltingResult(input) != null;

    public static final Predicate<ItemStack> MERCURIAL_TARGET = input -> {
        if (input == null) return false;
        IBlockState state = ItemHelper.stackToState(input);
        return state != null && !(state.getBlock().hasTileEntity(state)) && EMCHelper.doesItemHaveEmc(input);
    };

    private SlotPredicates() {}

}
