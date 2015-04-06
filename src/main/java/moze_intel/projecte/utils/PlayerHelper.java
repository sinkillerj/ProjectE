package moze_intel.projecte.utils;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SetFlyPKT;
import moze_intel.projecte.network.packets.StepHeightPKT;
import moze_intel.projecte.network.packets.SwingItemPKT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;

import java.lang.reflect.Field;

/**
 * Helper class for player-related methods.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class PlayerHelper
{
	public static void setPlayerFireImmunity(EntityPlayer player, boolean flag)
	{
		// TODO: Tag for Willie's magical obfuscation helper later
		Class c = Entity.class;
		Field field = c.getDeclaredFields()[52];
		field.setAccessible(true);

		try
		{
			field.setBoolean(player, flag);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setPlayerWalkSpeed(EntityPlayer player, float value)
	{
		// TODO: Tag for Willie's magical obfuscation helper later
		Class c = PlayerCapabilities.class;
		Field field = c.getDeclaredFields()[6];
		field.setAccessible(true);

		try
		{
			field.setFloat(player.capabilities, value);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void swingItem(EntityPlayerMP player)
	{
		PacketHandler.sendTo(new SwingItemPKT(), player);
	}

	public static void updateClientFlight(EntityPlayerMP player, boolean state)
	{
		PacketHandler.sendTo(new SetFlyPKT(state), player);
		player.capabilities.allowFlying = state;

		if (!state)
		{
			player.capabilities.isFlying = false;
		}
	}

	public static void updateClientStepHeight(EntityPlayerMP player, float value)
	{
		PacketHandler.sendTo(new StepHeightPKT(value), player);
	}
}
