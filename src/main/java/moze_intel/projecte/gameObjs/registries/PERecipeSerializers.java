package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.customRecipes.PERecipeSerializer;
import moze_intel.projecte.gameObjs.customRecipes.PhiloStoneSmeltingRecipe;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessKleinStar;
import moze_intel.projecte.gameObjs.customRecipes.RecipesCovalenceRepair;
import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class PERecipeSerializers {

	public static final PEDeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = new PEDeferredRegister<>(Registries.RECIPE_SERIALIZER, PECore.MODID);

	public static final PEDeferredHolder<RecipeSerializer<?>, RecipeSerializer<RecipesCovalenceRepair>> COVALENCE_REPAIR = RECIPE_SERIALIZERS.register("covalence_repair", () -> RecipesCovalenceRepair.SERIALIZER);
	public static final PEDeferredHolder<RecipeSerializer<?>, RecipeSerializer<RecipeShapelessKleinStar>> KLEIN = RECIPE_SERIALIZERS.register("crafting_shapeless_kleinstar", () -> PERecipeSerializer.wrapped(RecipeShapelessKleinStar::new));
	public static final PEDeferredHolder<RecipeSerializer<?>, RecipeSerializer<PhiloStoneSmeltingRecipe>> PHILO_STONE_SMELTING = RECIPE_SERIALIZERS.register("philo_stone_smelting", () -> PhiloStoneSmeltingRecipe.SERIALIZER);
}
