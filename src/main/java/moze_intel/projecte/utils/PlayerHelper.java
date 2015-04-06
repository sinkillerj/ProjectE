package moze_intel.projecte.utils;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.SetFlyPKT;
import moze_intel.projecte.network.packets.StepHeightPKT;
import moze_intel.projecte.network.packets.SwingItemPKT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Helper class for player-related methods.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class PlayerHelper
{
	public static void repelEntities(Entity player)
	{
		AxisAlignedBB bBox = AxisAlignedBB.getBoundingBox(player.posX - 5, player.posY - 5, player.posZ - 5, player.posX + 5, player.posY + 5, player.posZ + 5);
		List<Entity> list = player.worldObj.getEntitiesWithinAABB(Entity.class, bBox);

		for (Entity ent : list)
		{
			if (ent instanceof EntityPlayer)
			{
				continue;
			}

			Vec3 p = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
			Vec3 t = Vec3.createVectorHelper(ent.posX, ent.posY, ent.posZ);
			double distance = p.distanceTo(t) + 0.1D;

			Vec3 r = Vec3.createVectorHelper(t.xCoord - p.xCoord, t.yCoord - p.yCoord, t.zCoord - p.zCoord);

			ent.motionX += r.xCoord / 1.5D / distance;
			ent.motionY += r.yCoord / 1.5D / distance;
			ent.motionZ += r.zCoord / 1.5D / distance;
		}
	}

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
