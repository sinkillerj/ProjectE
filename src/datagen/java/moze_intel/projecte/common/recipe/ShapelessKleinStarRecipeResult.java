package moze_intel.projecte.common.recipe;

import com.google.gson.JsonObject;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;

public class ShapelessKleinStarRecipeResult implements IFinishedRecipe {

	private final IFinishedRecipe internal;

	public ShapelessKleinStarRecipeResult(IFinishedRecipe internal) {
		this.internal = internal;
	}

	@Override
	public void serializeRecipeData(@Nonnull JsonObject json) {
		internal.serializeRecipeData(json);
	}

	@Nonnull
	@Override
	public ResourceLocation getId() {
		return internal.getId();
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getType() {
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