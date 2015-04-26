package moze_intel.projecte.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;

/**
 * Helper class for anything that is accessed using reflection. Should only be accessed from other utils.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class ReflectionHelper
{
	// Mappings. Be sure to have MCP, obf, and SRG name. Not sure if obf name is necessary but doesn't hurt to have it.
	private static final String[] arrowInGroundNames = new String[] {"inGround", "i", "field_70254_i"};
	private static final String[] entityFireImmuneNames = new String[] {"isImmuneToFire", "ae", "field_70178_ae"};
	private static final String[] playerCapaWalkSpeedNames = new String[] {"walkSpeed", "g", "field_75097_g"};

	protected static boolean getArrowInGround(EntityArrow instance)
	{
		return cpw.mods.fml.relauncher.ReflectionHelper.getPrivateValue(EntityArrow.class, instance, arrowInGroundNames);
	}

	protected static void setEntityFireImmunity(Entity instance, boolean value)
	{
		cpw.mods.fml.relauncher.ReflectionHelper.setPrivateValue(Entity.class, instance, value, entityFireImmuneNames);
	}

	protected static void setPlayerCapabilityWalkspeed(PlayerCapabilities instance, float value)
	{
		cpw.mods.fml.relauncher.ReflectionHelper.setPrivateValue(PlayerCapabilities.class, instance, value, playerCapaWalkSpeedNames);
	}
}
