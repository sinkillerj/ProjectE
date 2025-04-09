package moze_intel.projecte.integration.recipe_viewer.jei;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.Optional;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.ICodecHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.recipe_viewer.RecipeViewerHelper;
import moze_intel.projecte.integration.recipe_viewer.WorldTransmuteEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class WorldTransmuteRecipeCategory implements IRecipeCategory<WorldTransmuteEntry> {

	public static final RecipeType<WorldTransmuteEntry> RECIPE_TYPE = new RecipeType<>(PECore.rl("world_transmutation"), WorldTransmuteEntry.class);
	private final IDrawable icon;

	public WorldTransmuteRecipeCategory(IGuiHelper guiHelper) {
		icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, PEItems.PHILOSOPHERS_STONE.asStack());
	}

	@NotNull
	@Override
	public RecipeType<WorldTransmuteEntry> getRecipeType() {
		return RECIPE_TYPE;
	}

	@NotNull
	@Override
	public Component getTitle() {
		return PELang.WORLD_TRANSMUTE.translate();
	}

	@Override
	public ResourceLocation getRegistryName(WorldTransmuteEntry recipe) {
		return recipe.syntheticId();
	}

	@Override
	public int getWidth() {
		return 90;
	}

	@Override
	public int getHeight() {
		return 26;
	}

	@NotNull
	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, @NotNull WorldTransmuteEntry recipe, @NotNull IFocusGroup focuses) {
		builder.addRecipeArrow().setPosition(25, 5);
	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull WorldTransmuteEntry recipe, @NotNull IFocusGroup focuses) {
		addIngredient(builder, RecipeIngredientRole.INPUT, 5, recipe.input());
		addIngredient(builder, RecipeIngredientRole.OUTPUT, 51, recipe.output());
		if (recipe.altOutput() != null) {
			addIngredient(builder, RecipeIngredientRole.OUTPUT, 68, recipe.altOutput());
		}
	}

	private void addIngredient(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int xPos, Either<ItemStack, FluidStack> ingredient) {
		IRecipeSlotBuilder slot = builder.addSlot(role, xPos, 5);
		ingredient.ifLeft(slot::addItemStack);
		Optional<FluidStack> right = ingredient.right();
		//noinspection OptionalIsPresent - Capturing lambda
		if (right.isPresent()) {
			slot.addIngredient(NeoForgeTypes.FLUID_STACK, right.get())
					.setFluidRenderer(FluidType.BUCKET_VOLUME, false, 16, 16);
		}
	}

	@Override
	public void getTooltip(@NotNull ITooltipBuilder tooltip, @NotNull WorldTransmuteEntry recipe, @NotNull IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
		if (mouseX >= 25 && mouseX < 49 && mouseY >= 5 && mouseY < 21) {
			tooltip.add(RecipeViewerHelper.getTransmuteDescription());
		}
	}

	@NotNull
	@Override
	public Codec<WorldTransmuteEntry> getCodec(@NotNull ICodecHelper codecHelper, @NotNull IRecipeManager recipeManager) {
		return WorldTransmuteEntry.CODEC;
	}
}