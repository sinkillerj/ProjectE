package moze_intel.projecte.utils;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.tags.ITag;
import net.minecraft.world.World;

public class EntityRandomizerHelper {

	@Nullable
	private static EntityType<? extends MobEntity> getEntityIfMob(EntityType<?> entityType) {
		try {
			//Try to cast the entity type to one extending a MobEntity, otherwise return null
			return (EntityType<? extends MobEntity>) entityType;
		} catch (ClassCastException e) {
			return null;
		}
	}

	private static List<EntityType<?>> filterEntityTypes(ITag<EntityType<?>> tag) {
		return tag.getAllElements().stream().filter(type -> getEntityIfMob(type) != null).collect(Collectors.toList());
	}

	public static MobEntity getRandomEntity(World world, MobEntity toRandomize) {
		//TODO - 1.16: Try to cleanup this logic as it is kind of messy, and validate our system for checking it is a mob entity even works properly
		EntityType<?> entType = getEntityIfMob(toRandomize.getType());
		if (entType != null) {
			//It should never be null because we are getting the type from a MobEntity, but double check that is the case
			if (PETags.Entities.RANDOMIZER_PEACEFUL.contains(entType)) {
				List<EntityType<?>> possibleEntities = filterEntityTypes(PETags.Entities.RANDOMIZER_PEACEFUL);
				return (MobEntity) CollectionHelper.getRandomListEntry(possibleEntities, entType).create(world);
			} else if (PETags.Entities.RANDOMIZER_HOSTILE.contains(entType)) {
				List<EntityType<?>> possibleEntities = filterEntityTypes(PETags.Entities.RANDOMIZER_HOSTILE);
				MobEntity ent = (MobEntity) CollectionHelper.getRandomListEntry(possibleEntities, entType).create(world);
				if (ent instanceof RabbitEntity) {
					((RabbitEntity) ent).setRabbitType(99);
				}
				return ent;
			}
		}
		if (world.rand.nextInt(2) == 0) {
			return EntityType.SLIME.create(world);
		}
		return EntityType.SHEEP.create(world);
	}
}