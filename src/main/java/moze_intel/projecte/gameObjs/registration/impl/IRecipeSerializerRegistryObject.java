package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

public class IRecipeSerializerRegistryObject<RECIPE extends Recipe<?>, SERIALIZER extends RecipeSerializer<RECIPE>> extends WrappedRegistryObject<SERIALIZER> {

	public IRecipeSerializerRegistryObject(RegistryObject<SERIALIZER> registryObject) {
		super(registryObject);
	}
}