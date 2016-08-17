package moze_intel.projecte.integration.jei.world_transmute;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

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
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper)
    {
        if (!(recipeWrapper instanceof WorldTransmuteRecipeWrapper))
            return;

        WorldTransmuteRecipeWrapper wrapper = (WorldTransmuteRecipeWrapper) recipeWrapper;
        boolean inputFluid = wrapper.getInputs().isEmpty();

        int itemSlots = 0;
        int fluidSlots = 0;

        if (inputFluid)
        {
            recipeLayout.getFluidStacks().init(fluidSlots, true, 16, 16, 16, 16, 1000, false, null);
            recipeLayout.getFluidStacks().set(fluidSlots, wrapper.getFluidInputs().get(0));
            fluidSlots++;
        }
        else
        {
            recipeLayout.getItemStacks().init(itemSlots, true, 16, 16);
            recipeLayout.getItemStacks().set(itemSlots, wrapper.getInputs().get(0));
            itemSlots++;
        }

        int xPos = 128;

        for (ItemStack s : wrapper.getOutputs())
        {
            recipeLayout.getItemStacks().init(itemSlots, false, xPos, 16);
            recipeLayout.getItemStacks().set(itemSlots, s);
            itemSlots++;
            xPos += 16;
        }

        xPos = 128;
        for (FluidStack s : wrapper.getFluidOutputs())
        {
            recipeLayout.getFluidStacks().init(fluidSlots, false, xPos, 32, 16, 16, 1000, false, null);
            recipeLayout.getFluidStacks().set(fluidSlots, s);
            fluidSlots++;
            xPos += 16;
        }
    }
}
