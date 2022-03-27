package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.WrappedRegistryObject;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class ItemRegistryObject<ITEM extends Item> extends WrappedRegistryObject<ITEM> implements ItemLike, IHasTranslationKey {

	public ItemRegistryObject(RegistryObject<ITEM> registryObject) {
		super(registryObject);
	}

	@NotNull
	@Override
	public ITEM asItem() {
		return get();
	}

	@Override
	public String getTranslationKey() {
		return get().getDescriptionId();
	}
}