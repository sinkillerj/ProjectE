package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import moze_intel.projecte.utils.EntityRandomizerHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.projecte.EntityRandomizer")
public class EntityRandomizer {

	@ZenCodeType.Method
	public static void addPeaceful(MCEntityType entityType) {
		EntityType<? extends MobEntity> living = getMob(entityType);
		if (isMob(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Add(living, entityType.getName(), true));
		}
	}

	@ZenCodeType.Method
	public static void removePeaceful(MCEntityType entityType) {
		EntityType<? extends MobEntity> living = getMob(entityType);
		if (isMob(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Remove(living, entityType.getName(), true));
		}
	}

	@ZenCodeType.Method
	public static void clearPeacefuls() {
		CraftTweakerAPI.apply(new EntityRandomizerAction.Clear(true));
	}

	@ZenCodeType.Method
	public static void addMob(MCEntityType entityType) {
		EntityType<? extends MobEntity> living = getMob(entityType);
		if (isMob(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Add(living, entityType.getName(), false));
		}
	}

	@ZenCodeType.Method
	public static void removeMob(MCEntityType entityType) {
		EntityType<? extends MobEntity> living = getMob(entityType);
		if (isMob(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Remove(living, entityType.getName(), false));
		}
	}

	@ZenCodeType.Method
	public static void clearMobs() {
		CraftTweakerAPI.apply(new EntityRandomizerAction.Clear(false));
	}

	private static EntityType<? extends MobEntity> getMob(MCEntityType entityType) {
		return entityType == null ? null : EntityRandomizerHelper.getEntityIfMob(entityType.getInternal());
	}

	private static boolean isMob(EntityType<? extends MobEntity> living) {
		if (living == null) {
			CraftTweakerAPI.logError("MCEntityType must be of a valid mob entity.");
			return false;
		}
		return true;
	}
}