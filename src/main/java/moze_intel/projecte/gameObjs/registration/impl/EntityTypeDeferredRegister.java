package moze_intel.projecte.gameObjs.registration.impl;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import moze_intel.projecte.gameObjs.entity.EntityNovaPrimed;
import moze_intel.projecte.gameObjs.entity.NoGravityThrowableProjectile;
import moze_intel.projecte.gameObjs.registration.PEDeferredRegister;
import moze_intel.projecte.utils.Constants;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityTypeDeferredRegister extends PEDeferredRegister<EntityType<?>> {

	public EntityTypeDeferredRegister(String modid) {
		super(Registries.ENTITY_TYPE, modid, EntityTypeRegistryObject::new);
	}

	public <ENTITY extends Entity> EntityTypeRegistryObject<ENTITY> registerBuilder(String name, Supplier<EntityType.Builder<ENTITY>> builder,
			UnaryOperator<EntityType.Builder<ENTITY>> modifier) {
		return (EntityTypeRegistryObject<ENTITY>) register(name, rl -> modifier.apply(builder.get()).build(ResourceKey.create(Registries.ENTITY_TYPE, rl)));
	}

	public <ENTITY extends Entity> EntityTypeRegistryObject<ENTITY> registerMisc(String name, EntityType.EntityFactory<ENTITY> factory,
			UnaryOperator<EntityType.Builder<ENTITY>> modifier) {
		return registerBuilder(name, () -> EntityType.Builder.of(factory, MobCategory.MISC), modifier);
	}

	public <ENTITY extends NoGravityThrowableProjectile> EntityTypeRegistryObject<ENTITY> registerNoGravThrowable(String name, EntityType.EntityFactory<ENTITY> factory) {
		return registerMisc(name, factory, builder -> builder
				.sized(0.5F, 0.5F)
				.clientTrackingRange(10)//Note: Vanilla throwables use a 4 for this, but as we have more velocity, we use a higher value
				.updateInterval(Constants.TICKS_PER_HALF_SECOND)
		);
	}

	public <ENTITY extends EntityNovaPrimed> EntityTypeRegistryObject<ENTITY> registerTnt(String name, EntityType.EntityFactory<ENTITY> factory) {
		return registerMisc(name, factory, builder -> builder
				//[VanillaCopy] from EntityType.TNT
				.fireImmune()
				.sized(0.98F, 0.98F)
				.eyeHeight(0.15F)
				.clientTrackingRange(10)
				.updateInterval(Constants.TICKS_PER_HALF_SECOND)
		);
	}
}
