package moze_intel.projecte.utils;

import com.google.common.base.Throwables;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.world.Explosion;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Helper class for anything that is accessed using reflection. Should only be accessed from other utils.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class ReflectionHelper
{
	// Mappings. Be sure to have MCP, obf, and SRG name. Not sure if obf name is necessary but doesn't hurt to have it.
	private static final String[] arrowInGroundNames = {"inGround", "a", "field_70254_i"};
	private static final String[] entityFireImmuneNames = {"isImmuneToFire", "Y", "field_70178_ae"};
	private static final String[] playerCapaWalkSpeedNames = {"walkSpeed", "g", "field_75097_g"};
	private static final String[] explosionSizeNames = {"explosionSize", "i", "field_77280_f"};
	private static final String[] updateScorePointsNames = { "updateScorePoints", "a", "func_184849_a" };

	private static final MethodHandle
		arrowInGround_getter, explosionSize_getter, explosionSize_setter,
		fireImmunity_setter, walkSpeed_setter,
		updateScorePoints;

	static {
		try {
			Field f = net.minecraftforge.fml.relauncher.ReflectionHelper.findField(EntityArrow.class, arrowInGroundNames);
			f.setAccessible(true);
			arrowInGround_getter = MethodHandles.publicLookup().unreflectGetter(f);

			f = net.minecraftforge.fml.relauncher.ReflectionHelper.findField(Entity.class, entityFireImmuneNames);
			f.setAccessible(true);
			fireImmunity_setter = MethodHandles.publicLookup().unreflectSetter(f);

			f = net.minecraftforge.fml.relauncher.ReflectionHelper.findField(Explosion.class, explosionSizeNames);
			f.setAccessible(true);
			explosionSize_getter = MethodHandles.publicLookup().unreflectGetter(f);

			f = net.minecraftforge.fml.relauncher.ReflectionHelper.findField(Explosion.class, explosionSizeNames);
			f.setAccessible(true);
			explosionSize_setter = MethodHandles.publicLookup().unreflectSetter(f);

			f = net.minecraftforge.fml.relauncher.ReflectionHelper.findField(PlayerCapabilities.class, playerCapaWalkSpeedNames);
			f.setAccessible(true);
			walkSpeed_setter = MethodHandles.publicLookup().unreflectSetter(f);

			Method m = net.minecraftforge.fml.relauncher.ReflectionHelper.findMethod(EntityPlayerMP.class, null, updateScorePointsNames, IScoreCriteria.class, int.class);
			m.setAccessible(true);
			updateScorePoints = MethodHandles.publicLookup().unreflect(m);
		} catch (IllegalAccessException e) {
			throw Throwables.propagate(e);
		}
	}

	protected static boolean getArrowInGround(EntityArrow instance)
	{
		try {
			return (boolean) arrowInGround_getter.invokeExact(instance);
		} catch (Throwable throwable) {
			return false;
		}
	}

	protected static float getExplosionSize(Explosion instance)
	{
		try {
			return (float) explosionSize_getter.invokeExact(instance);
		} catch (Throwable throwable) {
			return 0;
		}
	}

	protected static void setEntityFireImmunity(Entity instance, boolean value)
	{
		try {
			fireImmunity_setter.invokeExact(instance, value);
		} catch (Throwable ignored) {}
	}

	protected static void setExplosionSize(Explosion instance, float size)
	{
		try {
			explosionSize_setter.invokeExact(instance, size);
		} catch (Throwable ignored) {}
	}

	protected static void setPlayerCapabilityWalkspeed(PlayerCapabilities instance, float value)
	{
		try {
			walkSpeed_setter.invokeExact(instance, value);
		} catch (Throwable ignored) {}
	}

	protected static void updateScore(EntityPlayerMP player, IScoreCriteria objective, int score)
	{
		try {
			updateScorePoints.invokeExact(player, objective, score);
		} catch (Throwable ignored) {}
	}
}
