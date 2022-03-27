package moze_intel.projecte.integration.jei.collectors;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CollectorRecipeCategory implements IRecipeCategory<FuelUpgradeRecipe> {

	public static final RecipeType<FuelUpgradeRecipe> RECIPE_TYPE = new RecipeType<>(PECore.rl("collector"), FuelUpgradeRecipe.class);
	private final IDrawable background;
	private final IDrawable arrow;
	private final IDrawable icon;

	public CollectorRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(135, 48);
		arrow = guiHelper.drawableBuilder(PECore.rl("textures/gui/arrow.png"), 0, 0, 22, 15).setTextureSize(32, 32).build();
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(PEBlocks.COLLECTOR));
	}

	@NotNull
	@Override
	public RecipeType<FuelUpgradeRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@NotNull
	@Override
	@SuppressWarnings("removal")
	@Deprecated(forRemoval = true)
	public ResourceLocation getUid() {
		return getRecipeType().getUid();
	}

	@NotNull
	@Override
	@SuppressWarnings("removal")
	@Deprecated(forRemoval = true)
	public Class<? extends FuelUpgradeRecipe> getRecipeClass() {
		return getRecipeType().getRecipeClass();
	}

	@NotNull
	@Override
	public Component getTitle() {
		return PELang.JEI_COLLECTOR.translate();
	}

	@NotNull
	@Override
	public IDrawable getBackground() {
		return background;
	}

	@NotNull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull FuelUpgradeRecipe recipe, @NotNull IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 16, 16)
				.addItemStack(recipe.input());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 16)
				.addItemStack(recipe.output());
	}

	@Override
	public void draw(FuelUpgradeRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull PoseStack matrix, double mouseX, double mouseY) {
		Component emc = PELang.EMC.translate(recipe.upgradeEMC());
		Font fontRenderer = Minecraft.getInstance().font;
		int stringWidth = fontRenderer.width(emc);
		fontRenderer.draw(matrix, emc, (getBackground().getWidth() - stringWidth) / 2F, 5, 0x808080);
		arrow.draw(matrix, 55, 18);
	}
}