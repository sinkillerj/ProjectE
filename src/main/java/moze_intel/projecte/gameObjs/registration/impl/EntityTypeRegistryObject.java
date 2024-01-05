package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.PEDeferredHolder;
import moze_intel.projecte.utils.text.IHasTranslationKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class EntityTypeRegistryObject<ENTITY extends Entity> extends PEDeferredHolder<EntityType<?>, EntityType<ENTITY>> implements IHasTranslationKey {

	public EntityTypeRegistryObject(ResourceKey<EntityType<?>> key) {
		super(key);
	}

	@Override
	public String getTranslationKey() {
		return get().getDescriptionId();
	}
}