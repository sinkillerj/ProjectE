package moze_intel.projecte.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import moze_intel.projecte.playerData.IOHandler;
import moze_intel.projecte.utils.PELogger;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerSaveData {
    @SubscribeEvent
    public void playerSaveData(PlayerEvent.SaveToFile event)
    {
        IOHandler.saveData();
        PELogger.logInfo("Saved transmutation and alchemical bag data. ");
    }
}
