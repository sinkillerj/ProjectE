package moze_intel.projecte.integration.recipe_viewer.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import java.util.List;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.integration.recipe_viewer.FuelUpgradeRecipe;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public class CollectorEmiRecipe implements EmiRecipe {

	public static final EmiRecipeCategory CATEGORY = new PEEmiCategory("collector", PEBlocks.COLLECTOR, PELang.JEI_COLLECTOR);
	private final Identifier recipeId;
	private final List<EmiIngredient> input;
	private final List<EmiStack> output;
	private final long upgradeEMC;

	public CollectorEmiRecipe(FuelUpgradeRecipe recipe) {
		this.recipeId = recipe.syntheticId();
		this.upgradeEMC = recipe.upgradeEMC();
		this.input = List.of(EmiStack.of(recipe.input().value()));
		this.output = List.of(EmiStack.of(recipe.output().value()));
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return CATEGORY;
	}

	@Nullable
	@Override
	public Identifier getId() {
		return recipeId;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return input;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return output;
	}

	@Override
	public int getDisplayWidth() {
		return 80;
	}

	@Override
	public int getDisplayHeight() {
		return 36;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		Font fontRenderer = Minecraft.getInstance().font;
		int y = fontRenderer.lineHeight + 5;
		SlotWidget inputSlot = widgets.addSlot(input.getFirst(), 5, y);
		widgets.addSlot(output.getFirst(), 57, y)
				.recipeContext(this);

		Component emc = PELang.EMC.translate(upgradeEMC);
		int stringWidth = fontRenderer.width(emc);
		widgets.addText(emc, (getDisplayWidth() - stringWidth) / 2, 3, 0x808080, false);
		widgets.addFillingArrow(5 + inputSlot.getBounds().right(), y, (int) (5 * TimeUtil.MILLISECONDS_PER_SECOND));
	}

	@Nullable
	@Override
	public RecipeHolder<?> getBackingRecipe() {
		return null;
	}
}
