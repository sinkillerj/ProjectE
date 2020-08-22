package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;

public class IRecipeSerializerRegistryObject<RECIPE extends IRecipe<?>> extends WrappedRegistryObject<IRecipeSerializer<RECIPE>> {

    public IRecipeSerializerRegistryObject(RegistryObject<IRecipeSerializer<RECIPE>> registryObject) {
        super(registryObject);
    }
}