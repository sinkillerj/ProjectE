package moze_intel.projecte.api.event;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * This event is fired after a players transmutation knowledge is changed
 * This event is not cancelable, and has no result
 * This event is fired on MinecraftForge#EVENT_BUS
 */
public class PlayerKnowledgeChangeEvent extends Event
{
	public final UUID playerUUID;

    public PlayerKnowledgeChangeEvent(EntityPlayer entityPlayer)
    {
    	playerUUID = entityPlayer.getUniqueID();
    }
}
