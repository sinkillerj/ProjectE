package moze_intel.projecte.gameObjs.customRecipes;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

//From Mekanism's shaped recipe wrapper
public abstract class WrappedShapelessRecipe implements CraftingRecipe {

    private final ShapelessRecipe internal;

    protected WrappedShapelessRecipe(ShapelessRecipe internal) {
        this.internal = internal;
    }

    public ShapelessRecipe getInternal() {
        return internal;
    }

    @NotNull
    @Override
    public CraftingBookCategory category() {
        return internal.category();
    }

    @NotNull
    @Override
    public abstract ItemStack assemble(@NotNull CraftingInput inv);

    @Override
    public boolean matches(@NotNull CraftingInput inv, @NotNull Level world) {
        //Note: We do not override the matches method if it matches ignoring data components,
        // to ensure that we return the proper value for if there is a match that gives a proper output
        return internal.matches(inv, world) && !assemble(inv).isEmpty();
    }

    @NotNull
    @Override
    public NonNullList<ItemStack> getRemainingItems(@NotNull CraftingInput inv) {
        return internal.getRemainingItems(inv);
    }

    @Override
    public boolean isSpecial() {
        return internal.isSpecial();
    }

    @Override
    public boolean showNotification() {
        return internal.showNotification();
    }

    @NotNull
    @Override
    public String group() {
        return internal.group();
    }

    @Override
    public PlacementInfo placementInfo() {
        return internal.placementInfo();
    }
}
