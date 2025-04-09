package moze_intel.projecte.integration.recipe_viewer.emi;

import com.mojang.datafixers.util.Either;
import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TextureWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.recipe_viewer.RecipeViewerHelper;
import moze_intel.projecte.integration.recipe_viewer.WorldTransmuteEntry;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class WorldTransmuteEmiRecipe implements EmiRecipe {

	public static final EmiRecipeCategory CATEGORY = new PEEmiCategory("world_transmutation", PEItems.PHILOSOPHERS_STONE, PELang.WORLD_TRANSMUTE);

	private final ResourceLocation recipeId;
	private final List<EmiIngredient> input;
	private final List<EmiStack> outputs;

	public WorldTransmuteEmiRecipe(WorldTransmuteEntry recipe) {
		this.recipeId = recipe.syntheticId();
		this.input = List.of(asStack(recipe.input()));
		if (recipe.altOutput() == null) {
			this.outputs = List.of(asStack(recipe.output()));
		} else {
			this.outputs = List.of(asStack(recipe.output()), asStack(recipe.altOutput()));
		}
	}

	private static EmiStack asStack(Either<ItemStack, FluidStack> ingredient) {
		return ingredient.map(EmiStack::of, NeoForgeEmiStack::of);
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return CATEGORY;
	}

	@Nullable
	@Override
	public ResourceLocation getId() {
		return recipeId;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return input;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return outputs;
	}

	@Override
	public int getDisplayWidth() {
		return 100;
	}

	@Override
	public int getDisplayHeight() {
		return 26;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		SlotWidget inputSlot = widgets.addSlot(input.getFirst(), 5, 5)
				.drawBack(false);
		Bounds inputBounds = inputSlot.getBounds();
		TextureWidget arrow = widgets.addFillingArrow(3 + inputBounds.right(), inputBounds.y(), (int) (5 * TimeUtil.MILLISECONDS_PER_SECOND))
				.tooltip(List.of(ClientTooltipComponent.create(RecipeViewerHelper.getTransmuteDescription().getVisualOrderText())));
		int xPos = arrow.getBounds().right() + 3;
		for (EmiStack emiStack : outputs) {
			SlotWidget slot = widgets.addSlot(emiStack, xPos, inputBounds.y())
					.drawBack(false)
					.recipeContext(this);
			xPos += slot.getBounds().width() + 1;
		}
	}

	@Override
	public boolean supportsRecipeTree() {
		return false;
	}

	@Nullable
	@Override
	public RecipeHolder<?> getBackingRecipe() {
		return null;
	}
}