package moze_intel.projecte.playerData;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLCommonHandler;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TransmutationOffline
{
    private static Map<UUID, List<ItemStack>> cachedKnowledge = Maps.newHashMap();
    private static Map<UUID, Double> cachedEmc = Maps.newHashMap();
    private static Map<UUID, Boolean> cachedFullKnowledge = Maps.newHashMap();

    public static void cleanAll()
    {
        cachedKnowledge.clear();
        cachedEmc.clear();
        cachedFullKnowledge.clear();
    }

    public static void clear(UUID playerUUID)
    {
        cachedKnowledge.remove(playerUUID);
        cachedEmc.remove(playerUUID);
        cachedFullKnowledge.remove(playerUUID);
    }

    public static List<ItemStack> getKnowledge(UUID playerUUID)
    {
        if (!cachedKnowledge.containsKey(playerUUID))
        {
            cacheOfflineData(playerUUID);
        }
        return cachedKnowledge.get(playerUUID);
    }

    public static boolean hasKnowledgeForStack(ItemStack stack, UUID playerUUID)
    {
        List<ItemStack> knowledge = getKnowledge(playerUUID);
        if (knowledge != null)
        {
            for (ItemStack s : knowledge)
            {
                if (ItemHelper.basicAreStacksEqual(s, stack))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static double getEmc(UUID playerUUID)
    {
        if (!cachedEmc.containsKey(playerUUID))
        {
            cacheOfflineData(playerUUID);
        }
        return cachedEmc.get(playerUUID) == null ? Double.NaN : cachedEmc.get(playerUUID);
    }

    private static void cacheOfflineData(UUID playerUUID) {
        Preconditions.checkState(FMLCommonHandler.instance().getEffectiveSide().isServer(), "CRITICAL: Trying to read filesystem on client!!");
        File playerData = new File(DimensionManager.getCurrentSaveRootDirectory(), "playerdata");
        if (playerData.exists())
        {
            File player = new File(playerData, playerUUID.toString() + ".dat");
            if (player.exists() && player.isFile()) {
                try {
                    NBTTagCompound props = CompressedStreamTools.readCompressed(new FileInputStream(player)).getCompoundTag(TransmutationProps.PROP_NAME);
                    cachedEmc.put(playerUUID, props.getDouble("transmutationEmc"));
                    cachedFullKnowledge.put(playerUUID, props.getBoolean("tome"));

                    List<ItemStack> knowledge = Lists.newArrayList();
                    NBTTagList list = props.getTagList("knowledge", Constants.NBT.TAG_COMPOUND);
                    for (int i = 0; i < list.tagCount(); i++)
                    {
                        ItemStack item = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
                        if (item != null)
                        {
                            knowledge.add(item);
                        }
                    }
                    cachedKnowledge.put(playerUUID, knowledge);
                    PELogger.logDebug("Caching offline data for UUID: %s", playerUUID);
                } catch (IOException e) {
                    PELogger.logWarn("Failed to cache offline data for API calls for UUID: %s", playerUUID);
                }
            }
        }
    }
}
