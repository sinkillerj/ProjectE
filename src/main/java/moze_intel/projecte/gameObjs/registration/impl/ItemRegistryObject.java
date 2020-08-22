package moze_intel.projecte.gameObjs.registration.impl;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.fml.RegistryObject;

public class ItemRegistryObject<ITEM extends Item> extends WrappedRegistryObject<ITEM> implements IItemProvider, IHasTranslationKey {

    public ItemRegistryObject(RegistryObject<ITEM> registryObject) {
        super(registryObject);
    }

    @Nonnull
    @Override
    public ITEM asItem() {
        return get();
    }

    @Override
    public String getTranslationKey() {
        return get().getTranslationKey();
    }
}