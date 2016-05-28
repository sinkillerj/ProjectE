package moze_intel.projecte.api.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;

/**
 * This event is fired after a players transmutation knowledge is changed
 * This event is not cancelable, and has no result
 * This event is fired on MinecraftForge#EVENT_BUS
 */
public class PlayerKnowledgeChangeEvent extends Event
{
	private final UUID playerUUID;

    public PlayerKnowledgeChangeEvent(EntityPlayer entityPlayer)
    {
    	playerUUID = entityPlayer.getUniqueID();
    }

    public UUID getPlayerUUID()
    {
        return playerUUID;
    }

}
