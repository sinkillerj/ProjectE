package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class IRecipeSerializerDeferredRegister extends WrappedDeferredRegister<RecipeSerializer<?>> {

	public IRecipeSerializerDeferredRegister() {
		super(ForgeRegistries.RECIPE_SERIALIZERS);
	}

	public <RECIPE extends Recipe<?>, SERIALIZER extends RecipeSerializer<RECIPE>> IRecipeSerializerRegistryObject<RECIPE, SERIALIZER> register(String name,
			Supplier<SERIALIZER> sup) {
		return register(name, sup, IRecipeSerializerRegistryObject::new);
	}
}