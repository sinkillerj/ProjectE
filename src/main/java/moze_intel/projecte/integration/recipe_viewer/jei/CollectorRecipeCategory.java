package moze_intel.projecte.integration.recipe_viewer.jei;

import com.mojang.serialization.Codec;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.integration.recipe_viewer.FuelUpgradeRecipe;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CollectorRecipeCategory implements IRecipeCategory<FuelUpgradeRecipe> {

	public static final RecipeType<FuelUpgradeRecipe> RECIPE_TYPE = new RecipeType<>(PECore.rl("collector"), FuelUpgradeRecipe.class);
	private final IDrawable icon;

	public CollectorRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(PEBlocks.COLLECTOR));
	}

	@NotNull
	@Override
	public RecipeType<FuelUpgradeRecipe> getRecipeType() {
		return RECIPE_TYPE;
	}

	@NotNull
	@Override
	public Component getTitle() {
		return PELang.JEI_COLLECTOR.translate();
	}

	@Override
	public Identifier getRegistryName(FuelUpgradeRecipe recipe) {
		return recipe.syntheticId();
	}

	@Override
	public int getWidth() {
		return 80;
	}

	@Override
	public int getHeight() {
		return 36;
	}

	@NotNull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull FuelUpgradeRecipe recipe, @NotNull IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 5, 16)
				.addItemStack(new ItemStack(recipe.input()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 55, 16)
				.addItemStack(new ItemStack(recipe.output()));
	}

	@Override
	public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, @NotNull FuelUpgradeRecipe recipe, @NotNull IFocusGroup focuses) {
		builder.addRecipeArrow().setPosition(27, 16);
		builder.addText(PELang.EMC.translate(recipe.upgradeEMC()), getWidth() - 10, 11)
				.setPosition(5, 5)
				.setTextAlignment(HorizontalAlignment.CENTER);
	}

	@NotNull
	@Override
	public Codec<FuelUpgradeRecipe> getCodec(@NotNull ICodecHelper codecHelper, @NotNull IRecipeManager recipeManager) {
		return FuelUpgradeRecipe.CODEC;
	}
}
