package moze_intel.projecte.integration.jei.world_transmute;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorldTransmuteRecipeCategory implements IRecipeCategory
{
    public static final String UID = "pe.worldtransmute";
    private final IDrawable background;
    private final IDrawable arrow;
    private final IDrawable icon;
    private final String localizedName;

    public WorldTransmuteRecipeCategory(IGuiHelper guiHelper)
    {
        background = guiHelper.createBlankDrawable(175, 48);
        arrow = guiHelper.createDrawable(new ResourceLocation(PECore.MODID, "textures/gui/arrow.png"), 0, 0, 22, 15, 32, 32);
        icon = guiHelper.createDrawable(new ResourceLocation(PECore.MODID, "textures/items/philosophers_stone.png"), 0, 0, 16, 16, 16, 16);
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
        for (List<ItemStack> stacks : ingredients.getOutputs(ItemStack.class))
        {
            recipeLayout.getItemStacks().init(itemSlots, false, xPos, 16);
            recipeLayout.getItemStacks().set(itemSlots, stacks);
            itemSlots++;
            xPos += 16;
        }

        xPos = 128;
        for (List<FluidStack> stacks : ingredients.getOutputs(FluidStack.class))
        {
            recipeLayout.getFluidStacks().init(fluidSlots, false, xPos, 16, 16, 16, 1000, false, null);
            recipeLayout.getFluidStacks().set(fluidSlots, stacks);
            fluidSlots++;
            xPos += 16;
        }

    }

    @Nonnull
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }

    public static List<WorldTransmuteEntry> getAllTransmutations()
    {
        List<WorldTransmutations.Entry> allWorldTransmutations = WorldTransmutations.getWorldTransmutations();
        //All the ones that have a block state that can be rendered in JEI.
        //For example only render one pumpkin to melon transmutation
        List<WorldTransmuteEntry> visible = new ArrayList<>();
        allWorldTransmutations.forEach(entry -> {
            WorldTransmuteEntry e = new WorldTransmuteEntry(entry);
            if (e.isRenderable())
            {
                boolean alreadyHas;
                FluidStack inputFluid = e.getInputFluid();
                if (inputFluid != null)
                {
                    Fluid fluid = inputFluid.getFluid();
                    alreadyHas = visible.stream().map(WorldTransmuteEntry::getInputFluid).anyMatch(otherInputFluid -> otherInputFluid != null && fluid == otherInputFluid.getFluid());
                }
                else
                {
                    ItemStack inputItem = e.getInputItem();
                    alreadyHas = visible.stream().anyMatch(otherEntry -> ItemHelper.basicAreStacksEqual(inputItem, otherEntry.getInputItem()));
                }
                if (!alreadyHas)
                {
                    //Only add items that we haven't already had.
                    visible.add(e);
                }
            }
        });
        return visible;
    }
}
