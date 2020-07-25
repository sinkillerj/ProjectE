package moze_intel.projecte.utils;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.IMCMethods;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.InterModComms;

public class EntityRandomizerHelper {

	private static List<EntityType<? extends MobEntity>> PEACEFUL_DEFAULT = Collections.emptyList();
	private static List<EntityType<? extends MobEntity>> HOSTILE_DEFAULT = Collections.emptyList();

	private static List<EntityType<? extends MobEntity>> peacefulMobs = Collections.emptyList();
	private static List<EntityType<? extends MobEntity>> hostileMobs = Collections.emptyList();

	public static void init() {//TODO - 1.16: Add new mobs
		//Peaceful Mobs
		registerDefault(true, EntityType.SHEEP);
		registerDefault(true, EntityType.PIG);
		registerDefault(true, EntityType.COW);
		registerDefault(true, EntityType.MOOSHROOM);
		registerDefault(true, EntityType.CHICKEN);
		registerDefault(true, EntityType.BAT);
		registerDefault(true, EntityType.VILLAGER);
		registerDefault(true, EntityType.SQUID);
		registerDefault(true, EntityType.OCELOT);
		registerDefault(true, EntityType.WOLF);
		registerDefault(true, EntityType.HORSE);
		registerDefault(true, EntityType.RABBIT);
		registerDefault(true, EntityType.DONKEY);
		registerDefault(true, EntityType.MULE);
		registerDefault(true, EntityType.POLAR_BEAR);
		registerDefault(true, EntityType.LLAMA);
		registerDefault(true, EntityType.PARROT);
		registerDefault(true, EntityType.DOLPHIN);
		registerDefault(true, EntityType.COD);
		registerDefault(true, EntityType.SALMON);
		registerDefault(true, EntityType.PUFFERFISH);
		registerDefault(true, EntityType.TROPICAL_FISH);
		registerDefault(true, EntityType.TURTLE);
		registerDefault(true, EntityType.CAT);
		registerDefault(true, EntityType.FOX);
		registerDefault(true, EntityType.PANDA);
		registerDefault(true, EntityType.TRADER_LLAMA);
		registerDefault(true, EntityType.WANDERING_TRADER);

		//Hostile Mobs
		registerDefault(false, EntityType.ZOMBIE);
		registerDefault(false, EntityType.SKELETON);
		registerDefault(false, EntityType.CREEPER);
		registerDefault(false, EntityType.SPIDER);
		registerDefault(false, EntityType.ENDERMAN);
		registerDefault(false, EntityType.SILVERFISH);
		registerDefault(false, EntityType.ZOMBIFIED_PIGLIN);
		registerDefault(false, EntityType.GHAST);
		registerDefault(false, EntityType.BLAZE);
		registerDefault(false, EntityType.SLIME);
		registerDefault(false, EntityType.WITCH);
		registerDefault(false, EntityType.RABBIT);
		registerDefault(false, EntityType.ENDERMITE);
		registerDefault(false, EntityType.STRAY);
		registerDefault(false, EntityType.WITHER_SKELETON);
		registerDefault(false, EntityType.SKELETON_HORSE);
		registerDefault(false, EntityType.ZOMBIE_HORSE);
		registerDefault(false, EntityType.ZOMBIE_VILLAGER);
		registerDefault(false, EntityType.HUSK);
		registerDefault(false, EntityType.GUARDIAN);
		registerDefault(false, EntityType.EVOKER);
		registerDefault(false, EntityType.VEX);
		registerDefault(false, EntityType.VINDICATOR);
		registerDefault(false, EntityType.SHULKER);
		registerDefault(false, EntityType.DROWNED);
		registerDefault(false, EntityType.PHANTOM);
		registerDefault(false, EntityType.PILLAGER);
	}

	private static void registerDefault(boolean peaceful, EntityType<? extends MobEntity> entityType) {
		InterModComms.sendTo(PECore.MODID, peaceful ? IMCMethods.ENTITY_RANDOMIZATION_PEACEFUL : IMCMethods.ENTITY_RANDOMIZATION_HOSTILE, () -> entityType);
	}

	public static void setDefaultPeacefulRandomizers(List<EntityType<? extends MobEntity>> peacefulDefaults) {
		PEACEFUL_DEFAULT = ImmutableList.copyOf(peacefulDefaults);
		resetPeacefulMobs();
	}

	public static void setDefaultHostileRandomizers(List<EntityType<? extends MobEntity>> hostileDefaults) {
		HOSTILE_DEFAULT = ImmutableList.copyOf(hostileDefaults);
		resetHostileMobs();
	}

	@Nullable
	public static EntityType<? extends MobEntity> getEntityIfMob(EntityType<?> entityType) {
		try {
			//Try to cast the entity type to one extending a MobEntity, otherwise return null
			return (EntityType<? extends MobEntity>) entityType;
		} catch (ClassCastException e) {
			return null;
		}
	}

	public static boolean addPeacefulMob(EntityType<? extends MobEntity> type) {
		if (!peacefulMobs.contains(type)) {
			peacefulMobs.add(type);
			return true;
		}
		return false;
	}

	public static boolean removePeacefulMob(EntityType<? extends MobEntity> type) {
		return peacefulMobs.remove(type);
	}

	public static void clearPeacefulMobs() {
		peacefulMobs.clear();
	}

	public static void resetPeacefulMobs() {
		peacefulMobs = new ArrayList<>(PEACEFUL_DEFAULT);
	}

	public static boolean addHostileMob(EntityType<? extends MobEntity> type) {
		if (!hostileMobs.contains(type)) {
			hostileMobs.add(type);
			return true;
		}
		return false;
	}

	public static boolean removeHostileMob(EntityType<? extends MobEntity> type) {
		return hostileMobs.remove(type);
	}

	public static void clearHostileMobs() {
		hostileMobs.clear();
	}

	public static void resetHostileMobs() {
		hostileMobs = new ArrayList<>(HOSTILE_DEFAULT);
	}

	public static MobEntity getRandomEntity(World world, MobEntity toRandomize) {
		EntityType<? extends MobEntity> entType = getEntityIfMob(toRandomize.getType());
		if (entType != null) {
			//It should never be null because we are getting the type from a MobEntity, but double check that is the case
			if (peacefulMobs.contains(entType)) {
				return CollectionHelper.getRandomListEntry(peacefulMobs, entType).create(world);
			} else if (hostileMobs.contains(entType)) {
				MobEntity ent = CollectionHelper.getRandomListEntry(hostileMobs, entType).create(world);
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