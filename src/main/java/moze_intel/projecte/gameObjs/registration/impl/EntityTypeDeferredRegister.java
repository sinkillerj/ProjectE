package moze_intel.projecte.gameObjs.registration.impl;

import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public class EntityTypeDeferredRegister extends PEDeferredRegister<EntityType<?>> {

	public EntityTypeDeferredRegister(String modid) {
		super(Registries.ENTITY_TYPE, modid, EntityTypeRegistryObject::new);
	}

	public <ENTITY extends Entity> EntityTypeRegistryObject<ENTITY> register(String name, EntityType.Builder<ENTITY> builder) {
		return (EntityTypeRegistryObject<ENTITY>) register(name, () -> builder.build(name));
	}
}