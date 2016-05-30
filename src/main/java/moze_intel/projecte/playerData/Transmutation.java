package moze_intel.projecte.playerData;

import com.google.common.collect.ImmutableList;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.ItemStack;

import java.util.List;

public final class Transmutation
{
	private static List<ItemStack> cachedTomeKnowledge = ImmutableList.of();

	public static void clearCache() {
		cachedTomeKnowledge = ImmutableList.of();
	}

	public static void cacheFullKnowledge()
	{
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
		for (SimpleStack stack : EMCMapper.emc.keySet())
		{
			if (!stack.isValid())
			{
				continue;
			}

			try
			{
				ItemStack s = stack.toItemStack();
				s.stackSize = 1;

				//Apparently items can still not have EMC if they are in the EMC map.
				if (EMCHelper.doesItemHaveEmc(s) && EMCHelper.getEmcValue(s) > 0 && !ItemHelper.containsItemStack(cachedTomeKnowledge, s))
				{
					builder.add(s);
				}
			}
			catch (Exception e)
			{
				PELogger.logInfo("Failed to cache knowledge for " + stack + ": " + e.toString());
			}
		}

		cachedTomeKnowledge = builder.build();
	}

	public static List<ItemStack> getCachedTomeKnowledge()
	{
		return cachedTomeKnowledge;
	}
}
