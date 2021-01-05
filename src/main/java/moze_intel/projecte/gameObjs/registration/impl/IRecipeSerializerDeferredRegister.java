package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.Supplier;
import moze_intel.projecte.gameObjs.registration.WrappedDeferredRegister;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class IRecipeSerializerDeferredRegister extends WrappedDeferredRegister<IRecipeSerializer<?>> {

	public IRecipeSerializerDeferredRegister() {
		super(ForgeRegistries.RECIPE_SERIALIZERS);
	}

	public <RECIPE extends IRecipe<?>, SERIALIZER extends IRecipeSerializer<RECIPE>> IRecipeSerializerRegistryObject<RECIPE, SERIALIZER> register(String name,
			Supplier<SERIALIZER> sup) {
		return register(name, sup, IRecipeSerializerRegistryObject::new);
	}
}