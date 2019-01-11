package moze_intel.projecte.playerData;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Transmutation
{
	private static final List<ItemStack> CACHED_TOME_KNOWLEDGE = new ArrayList<>();

	public static void clearCache() {
		CACHED_TOME_KNOWLEDGE.clear();
	}

	public static void cacheFullKnowledge()
	{
		clearCache();
		for (Item item : EMCMapper.emc.keySet())
		{
			try
			{
				ItemStack s = new ItemStack(item);

				//Apparently items can still not have EMC if they are in the EMC map.
				if (EMCHelper.doesItemHaveEmc(s) && EMCHelper.getEmcValue(s) > 0 && !ItemHelper.containsItemStack(CACHED_TOME_KNOWLEDGE, s))
				{
					CACHED_TOME_KNOWLEDGE.add(s);
				}
			}
			catch (Exception e)
			{
				PECore.LOGGER.warn("Failed to cache knowledge for {}", item);
				e.printStackTrace();
			}
		}
	}

	public static List<ItemStack> getCachedTomeKnowledge()
	{
		return Collections.unmodifiableList(CACHED_TOME_KNOWLEDGE);
	}
}
