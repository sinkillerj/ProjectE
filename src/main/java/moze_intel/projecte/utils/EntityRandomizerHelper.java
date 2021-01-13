package moze_intel.projecte.utils;

import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.world.World;

public class EntityRandomizerHelper {

	public static MobEntity getRandomEntity(World world, MobEntity toRandomize) {
		EntityType<?> entType = toRandomize.getType();
		if (PETags.Entities.RANDOMIZER_PEACEFUL.contains(entType)) {
			return createRandomEntity(world, toRandomize, PETags.Entities.RANDOMIZER_PEACEFUL);
		} else if (PETags.Entities.RANDOMIZER_HOSTILE.contains(entType)) {
			MobEntity ent = createRandomEntity(world, toRandomize, PETags.Entities.RANDOMIZER_HOSTILE);
			if (ent instanceof RabbitEntity) {
				((RabbitEntity) ent).setRabbitType(99);
			}
			return ent;
		}
		if (world.rand.nextBoolean()) {
			return EntityType.SLIME.create(world);
		}
		return EntityType.SHEEP.create(world);
	}

	@Nullable
	private static MobEntity createRandomEntity(World world, Entity current, INamedTag<EntityType<?>> type) {
		EntityType<?> currentType = current.getType();
		EntityType<?> newType = CollectionHelper.getRandomListEntry(type.getAllElements(), currentType);
		if (currentType == newType) {
			//If the type is identical return null so that nothing happens
			return null;
		}
		Entity newEntity = newType.create(world);
		if (newEntity instanceof MobEntity) {
			return (MobEntity) newEntity;
		} else if (newEntity != null) {
			//There are "invalid" entries in the list that do not correspond to, kill the new entity
			newEntity.remove();
			// and log a warning
			PECore.LOGGER.warn("Invalid Entity type {} in mob randomizer tag {}. All entities in this tag are expected to be a mob.",
					newType.getRegistryName(), type.getName());
		}
		return null;
	}
}