package moze_intel.projecte.integration.jei.world_transmute;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.List;

public class WorldTransmuteRecipeCategory implements IRecipeCategory
{
    public static final String UID = "pe.worldtransmute";
    private final IDrawable background;
    private final IDrawable arrow;
    private final String localizedName;

    public WorldTransmuteRecipeCategory(IGuiHelper guiHelper)
    {
        background = guiHelper.createBlankDrawable(175, 48);
        arrow = guiHelper.createDrawable(new ResourceLocation("projecte:textures/gui/arrow.png"), 0, 0, 32, 32);
        localizedName = I18n.format("pe.nei.worldtransmute");
    }

    @Nonnull
    @Override
    public String getUid()
    {
        return UID;
    }

    @Nonnull
    @Override
    public String getTitle()
    {
        return localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft)
    {
        arrow.draw(minecraft, -30, 0);
    }

    @Override
    public void drawAnimations(@Nonnull Minecraft minecraft) {}

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {}

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients)
    {
        int itemSlots = 0;
        int fluidSlots = 0;

        int xPos = 16;
        for (List<FluidStack> s : ingredients.getInputs(FluidStack.class))
        {
            recipeLayout.getFluidStacks().init(fluidSlots, true, xPos, 16, 16, 16, 1000, false, null);
            recipeLayout.getFluidStacks().set(fluidSlots, s);
            fluidSlots++;
            xPos += 16;
        }

        xPos = 16;
        for (List<ItemStack> s : ingredients.getInputs(ItemStack.class))
        {
            recipeLayout.getItemStacks().init(itemSlots, true, xPos, 16);
            recipeLayout.getItemStacks().set(itemSlots, s);
            itemSlots++;
            xPos += 16;
        }

        xPos = 128;
        for (ItemStack s : ingredients.getOutputs(ItemStack.class))
        {
            recipeLayout.getItemStacks().init(itemSlots, false, xPos, 16);
            recipeLayout.getItemStacks().set(itemSlots, s);
            itemSlots++;
            xPos += 16;
        }

        xPos = 128;
        for (FluidStack s : ingredients.getOutputs(FluidStack.class))
        {
            recipeLayout.getFluidStacks().init(fluidSlots, false, xPos, 16, 16, 16, 1000, false, null);
            recipeLayout.getFluidStacks().set(fluidSlots, s);
            fluidSlots++;
            xPos += 16;
        }

    }
}
