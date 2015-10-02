package moze_intel.projecte.playerData;

import com.google.common.collect.Lists;
import moze_intel.projecte.api.event.PlayerKnowledgeChangeEvent;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeSyncPKT;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public final class Transmutation 
{
	private static final List<ItemStack> CACHED_TOME_KNOWLEDGE = Lists.newArrayList();

	public static void clearCache() {
		CACHED_TOME_KNOWLEDGE.clear();
	}

	public static void cacheFullKnowledge()
	{
		for (SimpleStack stack : EMCMapper.emcForCreation.keySet())
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
				if (EMCHelper.hasEmcValueForCreation(s) && EMCHelper.getEmcValueForCreation(s) > 0 && !ItemHelper.containsItemStack(CACHED_TOME_KNOWLEDGE, s))
				{
					CACHED_TOME_KNOWLEDGE.add(s);
				}
			}
			catch (Exception e)
			{
				PELogger.logInfo("Failed to cache knowledge for " + stack + ": " + e.toString());
			}
		}
	}

	public static List<ItemStack> getKnowledge(EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);
		if (data.hasFullKnowledge())
		{
			return CACHED_TOME_KNOWLEDGE;
		}
		else
		{
			return data.getKnowledge();
		}
	}

	public static void addKnowledge(ItemStack stack, EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);
		if (!data.hasFullKnowledge())
		{
			data.getKnowledge().add(stack);
			if (!player.worldObj.isRemote)
			{
				MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
			}
		}
	}

	public static void removeKnowledge(ItemStack stack, EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);
		if (!data.hasFullKnowledge())
		{
			Iterator<ItemStack> iter = data.getKnowledge().iterator();

			while (iter.hasNext())
			{
				if (ItemStack.areItemStacksEqual(stack, iter.next()))
				{
					iter.remove();
					if (!player.worldObj.isRemote)
					{
						MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
					}
					break;
				}
			}
		}
	}

	public static void setInputsAndLocks(ItemStack[] stacks, EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);
		data.setInputLocks(stacks);
	}

	public static ItemStack[] getInputsAndLock(EntityPlayer player)
	{
		ItemStack[] locks = TransmutationProps.getDataFor(player).getInputLocks();
		return Arrays.copyOf(locks, locks.length);
	}

	public static boolean hasKnowledgeForStack(ItemStack stack, EntityPlayer player)
	{
		if (hasFullKnowledge(player)) return EMCHelper.hasEmcValueForCreation(stack);
		TransmutationProps data = TransmutationProps.getDataFor(player);
		for (ItemStack s : data.getKnowledge())
		{
			if (ItemHelper.basicAreStacksEqual(s, stack))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean hasFullKnowledge(EntityPlayer player)
	{
		return TransmutationProps.getDataFor(player).hasFullKnowledge();
	}

	public static void setFullKnowledge(EntityPlayer player)
	{
		TransmutationProps.getDataFor(player).setFullKnowledge(true);
		if (!player.worldObj.isRemote)
		{
			MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
		}
	}

	public static void clearKnowledge(EntityPlayer player)
	{
		TransmutationProps data = TransmutationProps.getDataFor(player);
		data.setFullKnowledge(false);
		data.getKnowledge().clear();
		if (!player.worldObj.isRemote)
		{
			MinecraftForge.EVENT_BUS.post(new PlayerKnowledgeChangeEvent(player));
		}
	}

	public static double getEmc(EntityPlayer player)
	{
		return TransmutationProps.getDataFor(player).getTransmutationEmc();
	}

	public static void setEmc(EntityPlayer player, double emc)
	{
		TransmutationProps.getDataFor(player).setTransmutationEmc(emc);
	}

	public static void sync(EntityPlayer player)
	{
		PacketHandler.sendTo(new KnowledgeSyncPKT(TransmutationProps.getDataFor(player).saveForPacket()), (EntityPlayerMP) player);
		PELogger.logDebug("** SENT TRANSMUTATION DATA **");
	}
}
