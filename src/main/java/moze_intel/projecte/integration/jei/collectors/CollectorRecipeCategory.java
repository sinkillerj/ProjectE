package moze_intel.projecte.integration.jei.collectors;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import moze_intel.projecte.PECore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CollectorRecipeCategory implements IRecipeCategory
{
    public static final String UID = "pe.collector";
    private final IDrawable background;
    private final IDrawable arrow;
    private final IDrawable icon;
    private final String localizedName;

    public CollectorRecipeCategory(IGuiHelper guiHelper)
    {
        background = guiHelper.createBlankDrawable(175, 48);
        arrow = guiHelper.createDrawable(new ResourceLocation(PECore.MODID, "textures/gui/arrow.png"), 0, 0, 22, 15, 32, 32);
        icon = guiHelper.createDrawable(new ResourceLocation(PECore.MODID, "textures/blocks/collectors/front.png"), 0, 0, 16, 16, 16, 16);
        localizedName = I18n.format("pe.nei.collector");
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
    public String getModName() {
        return PECore.MODNAME;
    }

    @Nonnull
    @Override
    public IDrawable getBackground()
    {
        return background;
    }

    @Nullable
    @Override
    public IDrawable getIcon()
    {
        return icon;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft)
    {
        arrow.draw(minecraft, 75, 18);


    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients)
    {
        int itemSlots = 0;

        int xPos = 16;

        for (List<ItemStack> s : ingredients.getInputs(ItemStack.class))
        {
            recipeLayout.getItemStacks().init(itemSlots, true, xPos, 16);
            recipeLayout.getItemStacks().set(itemSlots, s);
            itemSlots++;
            xPos += 16;
        }

        xPos = 136;
        for (List<ItemStack> stacks : ingredients.getOutputs(ItemStack.class))
        {
            recipeLayout.getItemStacks().init(itemSlots, false, xPos, 16);
            recipeLayout.getItemStacks().set(itemSlots, stacks);
            itemSlots++;
            xPos += 16;
        }

    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }
}