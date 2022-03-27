package moze_intel.projecte.common.recipe;

import com.google.gson.JsonObject;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShapelessKleinStarRecipeResult implements FinishedRecipe {

	private final FinishedRecipe internal;

	public ShapelessKleinStarRecipeResult(FinishedRecipe internal) {
		this.internal = internal;
	}

	@Override
	public void serializeRecipeData(@NotNull JsonObject json) {
		internal.serializeRecipeData(json);
	}

	@NotNull
	@Override
	public ResourceLocation getId() {
		return internal.getId();
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getType() {
		//Overwrite it with our recipe serializer
		return PERecipeSerializers.KLEIN.get();
	}

	@Nullable
	@Override
	public JsonObject serializeAdvancement() {
		return internal.serializeAdvancement();
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancementId() {
		return internal.getAdvancementId();
	}
}