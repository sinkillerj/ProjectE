package moze_intel.projecte.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityDefinition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.registry.EntityEntry;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.projecte.EntityRandomizer")
public class EntityRandomizer
{
	@ZenMethod
	public static void addPeaceful(IEntityDefinition entityDefinition)
	{
		Class<? extends EntityLiving> living = getLiving(entityDefinition);
		if (isLiving(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Add(living, entityDefinition.getName(), true));
		}
	}

	@ZenMethod
	public static void removePeaceful(IEntityDefinition entityDefinition)
	{
		Class<? extends EntityLiving> living = getLiving(entityDefinition);
		if (isLiving(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Remove(living, entityDefinition.getName(), true));
		}
	}

	@ZenMethod
	public static void clearPeacefuls()
	{
		CraftTweakerAPI.apply(new EntityRandomizerAction.Clear(true));
	}

	@ZenMethod
	public static void addMob(IEntityDefinition entityDefinition)
	{
		Class<? extends EntityLiving> living = getLiving(entityDefinition);
		if (isLiving(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Add(living, entityDefinition.getName(), false));
		}
	}

	@ZenMethod
	public static void removeMob(IEntityDefinition entityDefinition)
	{
		Class<? extends EntityLiving> living = getLiving(entityDefinition);
		if (isLiving(living)) {
			CraftTweakerAPI.apply(new EntityRandomizerAction.Remove(living, entityDefinition.getName(), false));
		}
	}

	@ZenMethod
	public static void clearMobs()
	{
		CraftTweakerAPI.apply(new EntityRandomizerAction.Clear(false));
	}

	private static Class<? extends EntityLiving> getLiving(IEntityDefinition entityDefinition) {
		if (entityDefinition == null) {
			return null;
		}
		EntityEntry entry = (EntityEntry) entityDefinition.getInternal();
		Class<? extends Entity> entityClass = entry.getEntityClass();
		if (EntityLiving.class.isAssignableFrom(entityClass)) {
			return (Class<? extends EntityLiving>) entityClass;
		}
		return null;
	}

	private static boolean isLiving(Class<? extends EntityLiving> living) {
		if (living == null)
		{
			CraftTweakerAPI.logError("IEntityDefinition must be of a living entity.");
			return false;
		}
		return true;
	}
}