package moze_intel.projecte.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.world.Explosion;

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
	private static final String[] explosionSizeNames = new String[] {"explosionSize", "", ""};

	protected static boolean getArrowInGround(EntityArrow instance)
	{
		return net.minecraftforge.fml.relauncher.ReflectionHelper.getPrivateValue(EntityArrow.class, instance, arrowInGroundNames);
	}

	protected static float getExplosionSize(Explosion instance)
	{
		return net.minecraftforge.fml.relauncher.ReflectionHelper.getPrivateValue(Explosion.class, instance, explosionSizeNames);
	}

	protected static void setEntityFireImmunity(Entity instance, boolean value)
	{
		net.minecraftforge.fml.relauncher.ReflectionHelper.setPrivateValue(Entity.class, instance, value, entityFireImmuneNames);
	}

	protected static void setExplosionSize(Explosion instance, float size)
	{
		net.minecraftforge.fml.relauncher.ReflectionHelper.setPrivateValue(Explosion.class, instance, size, explosionSizeNames);
	}

	protected static void setPlayerCapabilityWalkspeed(PlayerCapabilities instance, float value)
	{
		net.minecraftforge.fml.relauncher.ReflectionHelper.setPrivateValue(PlayerCapabilities.class, instance, value, playerCapaWalkSpeedNames);
	}
}
