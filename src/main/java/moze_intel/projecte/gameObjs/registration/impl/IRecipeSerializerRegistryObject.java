package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;

public class IRecipeSerializerRegistryObject<RECIPE extends IRecipe<?>, SERIALIZER extends IRecipeSerializer<RECIPE>> extends WrappedRegistryObject<SERIALIZER> {

	public IRecipeSerializerRegistryObject(RegistryObject<SERIALIZER> registryObject) {
		super(registryObject);
	}
}