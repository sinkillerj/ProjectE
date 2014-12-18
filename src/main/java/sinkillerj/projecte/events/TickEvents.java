package sinkillerj.projecte.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import sinkillerj.projecte.handlers.PlayerChecks;
import sinkillerj.projecte.handlers.PlayerTimers;

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
