package moze_intel.projecte.gameObjs.customRecipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec.MapCodecCodec;
import java.util.function.Function;
import moze_intel.projecte.PECore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public class WrappedShapelessRecipeSerializer<RECIPE extends WrappedShapelessRecipe> implements RecipeSerializer<RECIPE> {

    private final Function<ShapelessRecipe, RECIPE> wrapper;
    private Codec<RECIPE> codec;

    public WrappedShapelessRecipeSerializer(Function<ShapelessRecipe, RECIPE> wrapper) {
        this.wrapper = wrapper;
    }

    @NotNull
    @Override
    public Codec<RECIPE> codec() {
        if (codec == null) {
            codec = ((MapCodecCodec<ShapelessRecipe>) RecipeSerializer.SHAPELESS_RECIPE.codec()).codec()
                    .xmap(wrapper, WrappedShapelessRecipe::getInternal).codec();
        }
        return codec;
    }

    @NotNull
    @Override
    public RECIPE fromNetwork(@NotNull FriendlyByteBuf buffer) {
        try {
            return wrapper.apply(RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(buffer));
        } catch (Exception e) {
            PECore.LOGGER.error("Error reading wrapped shaped recipe from packet.", e);
            throw e;
        }
    }

    @Override
    public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull RECIPE recipe) {
        try {
            RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe.getInternal());
        } catch (Exception e) {
            PECore.LOGGER.error("Error writing wrapped shaped recipe to packet.", e);
            throw e;
        }
    }
}