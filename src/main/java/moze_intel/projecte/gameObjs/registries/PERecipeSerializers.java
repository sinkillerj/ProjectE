package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.customRecipes.PhiloStoneSmeltingRecipe;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessKleinStar;
import moze_intel.projecte.gameObjs.customRecipes.RecipesCovalenceRepair;
import moze_intel.projecte.gameObjs.registration.impl.IRecipeSerializerDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.IRecipeSerializerRegistryObject;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

public class PERecipeSerializers {

	public static final IRecipeSerializerDeferredRegister RECIPE_SERIALIZERS = new IRecipeSerializerDeferredRegister(PECore.MODID);

	public static final IRecipeSerializerRegistryObject<RecipesCovalenceRepair, SimpleRecipeSerializer<RecipesCovalenceRepair>> COVALENCE_REPAIR = RECIPE_SERIALIZERS.register("covalence_repair", () -> new SimpleRecipeSerializer<>(RecipesCovalenceRepair::new));
	public static final IRecipeSerializerRegistryObject<RecipeShapelessKleinStar, RecipeShapelessKleinStar.Serializer> KLEIN = RECIPE_SERIALIZERS.register("crafting_shapeless_kleinstar", RecipeShapelessKleinStar.Serializer::new);
	public static final IRecipeSerializerRegistryObject<PhiloStoneSmeltingRecipe, SimpleRecipeSerializer<PhiloStoneSmeltingRecipe>> PHILO_STONE_SMELTING = RECIPE_SERIALIZERS.register("philo_stone_smelting", () -> new SimpleRecipeSerializer<>(PhiloStoneSmeltingRecipe::new));
}