package moze_intel.projecte.playerData;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OfflineTransmutationHandler
{
    public static OfflineTransmutationHandler instance = new OfflineTransmutationHandler();
    private MinecraftServer server;
    private Map<UUID, List<ItemStack>> offlineKnowledge = Maps.newHashMap();

    public void setServer(MinecraftServer server)
    {
        this.server = server;
    }

    public void cleanUp()
    {
        server = null;
        offlineKnowledge.clear();
    }

    public void getOfflineKnowledge(UUID playerUUID)
    {
        cacheOfflineKnowledge(playerUUID);
    }

    public void cacheOfflineKnowledge(UUID playerUUID)
    {
        if (!offlineKnowledge.containsKey(playerUUID))
        {
            // TODO read file
            List<ItemStack> list = Lists.newArrayList();
            offlineKnowledge.put(playerUUID, list);
        }
    }

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent evt)
    {
        if (offlineKnowledge.containsKey(evt.player.getGameProfile().getId()))
        {
            offlineKnowledge.remove(evt.player.getGameProfile().getId());
        }
    }
}
