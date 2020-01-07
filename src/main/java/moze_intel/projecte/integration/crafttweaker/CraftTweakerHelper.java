package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import moze_intel.projecte.utils.EntityRandomizerHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;

public class CraftTweakerHelper {

	public static boolean checkNonNull(Object value, String message) {
		if (value == null) {
			CraftTweakerAPI.logError(message);
			return false;
		}
		return true;
	}

	public static boolean validateEMC(long emc) {
		if (emc < 0) {
			CraftTweakerAPI.logError("EMC cannot be set to a negative number.");
			return false;
		}
		return true;
	}

	public static EntityType<? extends MobEntity> getMob(MCEntityType entityType) {
		return entityType == null ? null : EntityRandomizerHelper.getEntityIfMob(entityType.getInternal());
	}

	public static boolean isMob(EntityType<? extends MobEntity> living) {
		if (living == null) {
			CraftTweakerAPI.logError("MCEntityType must be of a valid mob entity.");
			return false;
		}
		return true;
	}
}