/*package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import moze_intel.projecte.integration.crafttweaker.actions.EntityRandomizerAction;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.projecte.EntityRandomizer")
public class EntityRandomizer {

	@ZenCodeType.Method
	public static void addPeacefulMob(MCEntityType entityType) {
		EntityType<? extends MobEntity> living = CraftTweakerHelper.getMob(entityType);
		if (CraftTweakerHelper.isMob(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Add(living, entityType.getName(), true));
		}
	}

	@ZenCodeType.Method
	public static void removePeacefulMob(MCEntityType entityType) {
		EntityType<? extends MobEntity> living = CraftTweakerHelper.getMob(entityType);
		if (CraftTweakerHelper.isMob(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Remove(living, entityType.getName(), true));
		}
	}

	@ZenCodeType.Method
	public static void clearPeacefulMobs() {
		CraftTweakerAPI.apply(new EntityRandomizerAction.Clear(true));
	}

	@ZenCodeType.Method
	public static void addHostileMob(MCEntityType entityType) {
		EntityType<? extends MobEntity> living = CraftTweakerHelper.getMob(entityType);
		if (CraftTweakerHelper.isMob(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Add(living, entityType.getName(), false));
		}
	}

	@ZenCodeType.Method
	public static void removeHostileMob(MCEntityType entityType) {
		EntityType<? extends MobEntity> living = CraftTweakerHelper.getMob(entityType);
		if (CraftTweakerHelper.isMob(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Remove(living, entityType.getName(), false));
		}
	}

	@ZenCodeType.Method
	public static void clearHostileMobs() {
		CraftTweakerAPI.apply(new EntityRandomizerAction.Clear(false));
	}
}*/