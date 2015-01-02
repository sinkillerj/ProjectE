package moze_intel.projecte.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import moze_intel.projecte.handlers.PlayerChecks;
import moze_intel.projecte.handlers.PlayerTimers;

public class TickEvents
{
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.END)
		{
			PlayerChecks.update();
			PlayerTimers.update();
		}
	}
}
