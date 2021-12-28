package moze_intel.projecte.integration.jei.collectors;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CollectorRecipeCategory implements IRecipeCategory<FuelUpgradeRecipe> {

	public static final ResourceLocation UID = PECore.rl("collector");
	private final IDrawable background;
	private final IDrawable arrow;
	private final IDrawable icon;

	public CollectorRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(135, 48);
		arrow = guiHelper.drawableBuilder(PECore.rl("textures/gui/arrow.png"), 0, 0, 22, 15).setTextureSize(32, 32).build();
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(PEBlocks.COLLECTOR));
	}

	@Nonnull
	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	@Nonnull
	public Class<FuelUpgradeRecipe> getRecipeClass() {
		return FuelUpgradeRecipe.class;
	}

	@Nonnull
	@Override
	public Component getTitle() {
		return PELang.JEI_COLLECTOR.translate();
	}

	@Nonnull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Nonnull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(@Nonnull FuelUpgradeRecipe o, @Nonnull IIngredients ingredients) {
		ingredients.setInput(VanillaTypes.ITEM, o.getInput());
		ingredients.setOutput(VanillaTypes.ITEM, o.getOutput());
	}

	@Override
	public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull FuelUpgradeRecipe o, @Nonnull IIngredients ingredients) {
		int itemSlots = 0;
		int xPos = 16;
		for (List<ItemStack> s : ingredients.getInputs(VanillaTypes.ITEM)) {
			recipeLayout.getItemStacks().init(itemSlots, true, xPos, 16);
			recipeLayout.getItemStacks().set(itemSlots, s);
			itemSlots++;
			xPos += 16;
		}
		xPos = 104;
		for (List<ItemStack> stacks : ingredients.getOutputs(VanillaTypes.ITEM)) {
			recipeLayout.getItemStacks().init(itemSlots, false, xPos, 16);
			recipeLayout.getItemStacks().set(itemSlots, stacks);
			itemSlots++;
			xPos += 16;
		}
	}

	@Override
	public void draw(FuelUpgradeRecipe recipe, @Nonnull PoseStack matrix, double mouseX, double mouseY) {
		Component emc = PELang.EMC.translate(recipe.getUpgradeEMC());
		Font fontRenderer = Minecraft.getInstance().font;
		int stringWidth = fontRenderer.width(emc);
		fontRenderer.draw(matrix, emc, (getBackground().getWidth() - stringWidth) / 2F, 5, 0x808080);
		arrow.draw(matrix, 55, 18);
	}
}