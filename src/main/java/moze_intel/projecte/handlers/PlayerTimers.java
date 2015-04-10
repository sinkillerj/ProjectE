package moze_intel.projecte.handlers;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;

import java.util.LinkedHashMap;

public final class PlayerTimers
{
	private static final LinkedHashMap<String, TimerSet> MAP = Maps.newLinkedHashMap();

	public static void update()
	{
		for (TimerSet timers : MAP.values())
		{
			if (timers.repair.shouldUpdate)
			{
				if (timers.repair.tickCount < 19)
				{
					timers.repair.tickCount++;
				}

				timers.repair.shouldUpdate = false;
			}

			if (timers.heal.shouldUpdate)
			{
				if (timers.heal.tickCount < 19)
				{
					timers.heal.tickCount++;
				}

				timers.heal.shouldUpdate = false;
			}

			if (timers.feed.shouldUpdate)
			{
				if (timers.feed.tickCount < 19)
				{
					timers.feed.tickCount++;
				}

				timers.feed.shouldUpdate = false;
			}
		}
	}

	public static void registerPlayer(EntityPlayer player)
	{
		MAP.put(player.getCommandSenderName(), new TimerSet());
	}

	public static void removePlayer(EntityPlayer player)
	{
		MAP.remove(player.getCommandSenderName());
	}

	public static void activateRepair(EntityPlayer player)
	{
		MAP.get(player.getCommandSenderName()).repair.shouldUpdate = true;
	}

	public static void activateHeal(EntityPlayer player)
	{
		MAP.get(player.getCommandSenderName()).heal.shouldUpdate = true;
	}

	public static void activateFeed(EntityPlayer player)
	{
		MAP.get(player.getCommandSenderName()).feed.shouldUpdate = true;
	}

	public static boolean canRepair(EntityPlayer player)
	{
		Timer timer = MAP.get(player.getCommandSenderName()).repair;

		if (timer.tickCount >= 19)
		{
			timer.tickCount = 0;
			timer.shouldUpdate = false;
			return true;
		}

		return false;
	}

	public static boolean canHeal(EntityPlayer player)
	{
		Timer timer = MAP.get(player.getCommandSenderName()).heal;

		if (timer.tickCount >= 19)
		{
			timer.tickCount = 0;
			timer.shouldUpdate = false;
			return true;
		}

		return false;
	}

	public static boolean canFeed(EntityPlayer player)
	{
		Timer timer = MAP.get(player.getCommandSenderName()).feed;

		if (timer.tickCount >= 19)
		{
			timer.tickCount = 0;
			timer.shouldUpdate = false;
			return true;
		}

		return false;
	}

	private static class TimerSet
	{
		public Timer repair;
		public Timer heal;
		public Timer feed;

		public TimerSet()
		{
			repair = new Timer();
			heal = new Timer();
			feed = new Timer();
		}
	}

	private static class Timer
	{
		public short tickCount;
		public boolean shouldUpdate;

		public Timer()
		{
			tickCount = 0;
			shouldUpdate = false;
		}

		@Override
		public String toString()
		{
			return "TICKS: " + tickCount + "\n" + "ACTIVE: " + shouldUpdate;
		}
	}
}
