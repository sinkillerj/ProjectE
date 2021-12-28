package moze_intel.projecte.utils;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.tags.Tag;
import net.minecraft.tags.Tag.Named;
import net.minecraft.world.level.Level;

public class EntityRandomizerHelper {

	public static Mob getRandomEntity(Level world, Mob toRandomize) {
		EntityType<?> entType = toRandomize.getType();
		boolean isPeaceful = PETags.Entities.RANDOMIZER_PEACEFUL.contains(entType);
		boolean isHostile = PETags.Entities.RANDOMIZER_HOSTILE.contains(entType);
		if (isPeaceful && isHostile) {
			//If it is in both lists do some extra checks to see if it really is peaceful
			// currently this only includes our special casing for killer rabbits
			if (toRandomize instanceof Rabbit rabbit && rabbit.getRabbitType() == 99) {
				//Killer rabbits are not peaceful
				isPeaceful = false;
			}
		}
		if (isPeaceful) {
			return createRandomEntity(world, toRandomize, PETags.Entities.RANDOMIZER_PEACEFUL);
		} else if (isHostile) {
			Mob ent = createRandomEntity(world, toRandomize, PETags.Entities.RANDOMIZER_HOSTILE);
			if (ent instanceof Rabbit rabbit) {
				rabbit.setRabbitType(99);
			}
			return ent;
		}
		if (world.random.nextBoolean()) {
			return EntityType.SLIME.create(world);
		}
		return EntityType.SHEEP.create(world);
	}

	@Nullable
	private static Mob createRandomEntity(Level world, Entity current, Named<EntityType<?>> type) {
		EntityType<?> currentType = current.getType();
		EntityType<?> newType = getRandomTagEntry(world.getRandom(), type, currentType);
		if (currentType == newType) {
			//If the type is identical return null so that nothing happens
			return null;
		}
		Entity newEntity = newType.create(world);
		if (newEntity instanceof Mob) {
			return (Mob) newEntity;
		} else if (newEntity != null) {
			//There are "invalid" entries in the list that do not correspond to, kill the new entity
			newEntity.discard();//TODO - 1.18: Review all our calls of discard because maybe some were wrong
			// and log a warning
			PECore.LOGGER.warn("Invalid Entity type {} in mob randomizer tag {}. All entities in this tag are expected to be a mob.",
					newType.getRegistryName(), type.getName());
		}
		return null;
	}

	private static <T> T getRandomTagEntry(Random random, Tag<T> tag, T toExclude) {
		List<T> list = tag.getValues();
		if (list.isEmpty() || list.size() == 1 && list.contains(toExclude)) {
			return toExclude;
		}
		T obj;
		do {
			obj = tag.getRandomElement(random);
		} while (obj.equals(toExclude));
		return obj;
	}
}