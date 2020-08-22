package moze_intel.projecte.gameObjs.registration.impl;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;

public class ItemRegistryObject<ITEM extends Item> extends WrappedRegistryObject<ITEM> implements IItemProvider {

    public ItemRegistryObject(RegistryObject<ITEM> registryObject) {
        super(registryObject);
    }

    @Nonnull
    @Override
    public ITEM asItem() {
        return get();
    }
}