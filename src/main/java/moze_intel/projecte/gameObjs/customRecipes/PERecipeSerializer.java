package moze_intel.projecte.gameObjs.customRecipes;

import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public final class PERecipeSerializer {

	private PERecipeSerializer() {
	}

	public static <RECIPE extends WrappedShapelessRecipe> RecipeSerializer<RECIPE> wrapped(Function<ShapelessRecipe, RECIPE> wrapper) {
		return new RecipeSerializer<>(
				ShapelessRecipe.MAP_CODEC.xmap(wrapper, WrappedShapelessRecipe::getInternal),
				ShapelessRecipe.STREAM_CODEC.map(wrapper, WrappedShapelessRecipe::getInternal)
		);
	}
}
