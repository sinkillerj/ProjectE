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
	public void serialize(@Nonnull JsonObject json) {
		internal.serialize(json);
	}

	@Nonnull
	@Override
	public ResourceLocation getID() {
		return internal.getID();
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		//Overwrite it with our recipe serializer
		return PERecipeSerializers.KLEIN.get();
	}

	@Nullable
	@Override
	public JsonObject getAdvancementJson() {
		return internal.getAdvancementJson();
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancementID() {
		return internal.getAdvancementID();
	}
}