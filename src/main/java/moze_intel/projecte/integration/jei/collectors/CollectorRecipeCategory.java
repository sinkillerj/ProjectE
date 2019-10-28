package moze_intel.projecte.integration.jei.collectors;

import java.awt.Color;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CollectorRecipeCategory implements IRecipeCategory<FuelUpgradeRecipe> {

	public static final ResourceLocation UID = new ResourceLocation(PECore.MODID, "collector");
	private final IDrawable background;
	private final IDrawable arrow;
	private final IDrawable icon;
	private final String localizedName;

	public CollectorRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(135, 48);
		arrow = guiHelper.drawableBuilder(new ResourceLocation(PECore.MODID, "textures/gui/arrow.png"), 0, 0, 22, 15)
				.setTextureSize(32, 32).build();
		icon = guiHelper.createDrawableIngredient(new ItemStack(ObjHandler.collectorMK1));
		localizedName = I18n.format("pe.jei.collector");
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
	public String getTitle() {
		return localizedName;
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
	public void draw(FuelUpgradeRecipe recipe, double mouseX, double mouseY) {
		String emc = recipe.getUpgradeEMC() + " EMC";
		FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
		int stringWidth = fontRenderer.getStringWidth(emc);
		fontRenderer.drawString(emc, (getBackground().getWidth() / 2F) - (stringWidth / 2F), 5, Color.GRAY.getRGB());
		arrow.draw(55, 18);
	}
}