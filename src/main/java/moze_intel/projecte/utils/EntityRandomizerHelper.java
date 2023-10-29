package moze_intel.projecte.utils;

import java.util.Optional;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.PETags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Rabbit.Variant;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.Nullable;

public class EntityRandomizerHelper {

	@Nullable
	public static Mob getRandomEntity(Level level, Mob toRandomize) {
		EntityType<?> entType = toRandomize.getType();
		boolean isPeaceful = entType.is(PETags.Entities.RANDOMIZER_PEACEFUL);
		boolean isHostile = entType.is(PETags.Entities.RANDOMIZER_HOSTILE);
		if (isPeaceful && isHostile) {
			//If it is in both lists do some extra checks to see if it really is peaceful
			// currently this only includes our special casing for killer rabbits
			if (toRandomize instanceof Rabbit rabbit && rabbit.getVariant() == Variant.EVIL) {
				//Killer rabbits are not peaceful
				isPeaceful = false;
			}
		}
		if (isPeaceful) {
			return createRandomEntity(level, toRandomize, PETags.Entities.RANDOMIZER_PEACEFUL);
		} else if (isHostile) {
			Mob ent = createRandomEntity(level, toRandomize, PETags.Entities.RANDOMIZER_HOSTILE);
			if (ent instanceof Rabbit rabbit) {
				rabbit.setVariant(Variant.EVIL);
			}
			return ent;
		}
		return null;
	}

	@Nullable
	private static Mob createRandomEntity(Level level, Entity current, TagKey<EntityType<?>> type) {
		ITag<EntityType<?>> tag = LazyTagLookup.tagManager(ForgeRegistries.ENTITY_TYPES).getTag(type);
		EntityType<?> currentType = current.getType();
		EntityType<?> newType = getRandomTagEntry(level.getRandom(), tag, currentType);
		if (currentType == newType) {
			//If the type is identical return null so that nothing happens
			return null;
		}
		Entity newEntity = newType.create(level);
		if (newEntity instanceof Mob) {
			return (Mob) newEntity;
		} else if (newEntity != null) {
			//There are "invalid" entries in the list that do not correspond to, kill the new entity
			newEntity.discard();
			// and log a warning
			PECore.LOGGER.warn("Invalid Entity type {} in mob randomizer tag {}. All entities in this tag are expected to be a mob.", RegistryUtils.getName(newType),
					type.location());
		}
		return null;
	}

	private static <T> T getRandomTagEntry(RandomSource random, ITag<T> tag, T toExclude) {
		int size = tag.size();
		if (size == 0 || size == 1 && tag.contains(toExclude)) {
			return toExclude;
		}
		Optional<T> obj;
		do {
			obj = tag.getRandomElement(random);
		} while (obj.isPresent() && obj.get().equals(toExclude));
		//Fallback to base
		return obj.orElse(toExclude);
	}
}