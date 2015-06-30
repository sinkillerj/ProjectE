package moze_intel.projecte.playerData;

import com.google.common.collect.Maps;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class TransmutationOffline {
    private static Map<UUID, NBTTagCompound> cachedOfflineKnowledge = Maps.newHashMap();

    public static void cleanAll() {
        cachedOfflineKnowledge.clear();
    }

    public static void clear(UUID playerUUID) {
        cachedOfflineKnowledge.remove(playerUUID);
    }

    public static List<ItemStack> getKnowledge(UUID playerUUID) {
        cacheOfflineData(playerUUID);
    }

    public static boolean hasKnowledgeForStack(UUID playerUUID) {
        cacheOfflineData(playerUUID);
    }

    public static boolean hasFullKnowledge(UUID playerUUID) {
        cacheOfflineData(playerUUID);
    }

    public static double getEmc(UUID playerUUID) {
        cacheOfflineData(playerUUID);
    }

    private static void cacheOfflineData(UUID playerUUID) {
        if (!cachedOfflineKnowledge.containsKey(playerUUID)) {
            File playerData = new File(MinecraftServer.getServer().getEntityWorld().getSaveHandler().getWorldDirectory(), "playerdata");
            if (playerData.exists())
            {
                File player = new File(playerData, playerUUID.toString() + ".dat");
                if (player.exists() && player.isFile()) {
                    try {
                        NBTTagCompound tag = CompressedStreamTools.readCompressed(new FileInputStream(player));
                        cachedOfflineKnowledge.put(playerUUID, tag.getCompoundTag(TransmutationProps.PROP_NAME));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
