package moze_intel.projecte.integration.jei.collectors;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.*;

public class FuelUpgradeRecipe implements IRecipeWrapper {

    private ItemStack input;
    private ItemStack output;
    private long upgradeEMC;

    public FuelUpgradeRecipe(ItemStack input, ItemStack output){
        this.input = input;
        this.output = output;
        this.upgradeEMC = EMCHelper.getEmcValue(output) - EMCHelper.getEmcValue(input);
    }

    public ItemStack getInput(){
        return input;
    }

    public ItemStack getOutput(){
        return output;
    }

    public long getUpgradeEMC(){
        return upgradeEMC;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(ItemStack.class, getInput());
        ingredients.setOutput(ItemStack.class, getOutput());
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        String emc = Long.toString(getUpgradeEMC());
        FontRenderer fontRenderer = minecraft.fontRenderer;
        int stringWidth = fontRenderer.getStringWidth(emc);
        fontRenderer.drawString(emc, (recipeWidth / 2) - (stringWidth / 2), 5, Color.gray.getRGB());

    }

    @Override
    public java.util.List<String> getTooltipStrings(int mouseX, int mouseY) {

        if(mouseX > 67 && mouseX < 107)
            if(mouseY > 0 && mouseY < 20)
                return Collections.singletonList("EMC needed to upgrade");

        return Collections.emptyList();
    }
}