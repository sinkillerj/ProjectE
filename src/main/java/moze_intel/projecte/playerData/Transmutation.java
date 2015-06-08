package moze_intel.projecte.playerData;

import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientKnowledgeSyncPKT;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public final class Transmutation 
{
	private static final LinkedHashMap<String, LinkedList<ItemStack>> MAP = new LinkedHashMap<String, LinkedList<ItemStack>>();
	private static final LinkedHashMap<String, Double> EMC_STORAGE = new LinkedHashMap<String, Double>();
	private static final LinkedList<String> TOME_KNOWLEDGE = new LinkedList<String>();
	private static final LinkedList<ItemStack> CACHED_TOME_KNOWLEDGE = new LinkedList<ItemStack>();
	
	public static void cacheFullKnowledge()
	{
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
				if (EMCHelper.doesItemHaveEmc(s) && EMCHelper.getEmcValue(s) > 0 && !ItemHelper.containsItemStack(CACHED_TOME_KNOWLEDGE, s))
				{
					CACHED_TOME_KNOWLEDGE.add(s);
				}
			}
			catch (Exception e)
			{
				PELogger.logInfo("Failed to cache knowledge for "+ stack + ": " + e.toString());
			}
		}
	}

	public static List<ItemStack> newGetKnowledge(EntityPlayer player)
	{
		PETransmutation data = PETransmutation.getDataFor(player);
		if (data.hasFullKnowledge())
		{
			return CACHED_TOME_KNOWLEDGE;
		}
		else
		{
			return data.getKnowledge();
		}
	}

	public static void newAddKnowledge(ItemStack stack, EntityPlayer player)
	{
		PETransmutation data = PETransmutation.getDataFor(player);
		if (!data.hasFullKnowledge())
		{
			data.getKnowledge().add(stack);
		}
	}

	public static void newRemoveKnowledge(ItemStack stack, EntityPlayer player)
	{
		PETransmutation data = PETransmutation.getDataFor(player);
		if (!data.hasFullKnowledge())
		{
			Iterator<ItemStack> iter = data.getKnowledge().iterator();

			while (iter.hasNext())
			{
				if (ItemStack.areItemStacksEqual(stack, iter.next()))
				{
					iter.remove();
					break;
				}
			}
		}
	}

	public static boolean newHasKnowledgeForStack(EntityPlayer player, ItemStack stack)
	{
		PETransmutation data = PETransmutation.getDataFor(player);
		for (ItemStack s : data.getKnowledge())
		{
			if (ItemStack.areItemStacksEqual(s, stack))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean newHasFullKnowledge(EntityPlayer player)
	{
		return PETransmutation.getDataFor(player).hasFullKnowledge();
	}

	public static void newSetFullKnowledge(EntityPlayer player)
	{
		PETransmutation.getDataFor(player).setFullKnowledge(true);
	}

	public static void newClearKnowledge(EntityPlayer player)
	{
		PETransmutation data = PETransmutation.getDataFor(player);
		data.setFullKnowledge(false);
		data.getKnowledge().clear();
	}

	public static double newGetEmc(EntityPlayer player)
	{
		return PETransmutation.getDataFor(player).getTransmutationEmc();
	}

	public static void newSetEmc(EntityPlayer player, double emc)
	{
		PETransmutation.getDataFor(player).setTransmutationEmc(emc);
	}

	public static LinkedList<ItemStack> getKnowledge(String username)
	{
		if (TOME_KNOWLEDGE.contains(username))
		{
			return CACHED_TOME_KNOWLEDGE;
		}

		if (MAP.containsKey(username))
		{
			LinkedList<ItemStack> list = MAP.get(username);

			for (int i = 0; i < list.size(); i++)
			{
				if (!EMCHelper.doesItemHaveEmc(list.get(i)))
				{
					list.remove(i);
				}
			}

			return list;
		}

		return new LinkedList<ItemStack>();
	}

	public static void addToKnowledge(String username, ItemStack stack)
	{
		if (TOME_KNOWLEDGE.contains(username))
		{
			return;
		}

		if (MAP.containsKey(username))
		{
			MAP.get(username).add(stack);
		}
		else
		{
			LinkedList<ItemStack> list = new LinkedList<ItemStack>();
			list.add(stack);
			MAP.put(username, list);
		}

		IOHandler.markDirty();
	}

	public static void removeFromKnowledge(String username, ItemStack stack)
	{
		if (TOME_KNOWLEDGE.contains(username))
		{
			return;
		}

		if (MAP.containsKey(username))
		{
			Iterator<ItemStack> iter = MAP.get(username).iterator();

			while (iter.hasNext())
			{
				ItemStack knownstack = iter.next();

				if (knownstack.areItemStacksEqual(knownstack, stack))
				{
					MAP.get(username).remove(knownstack);
					IOHandler.markDirty();
					break;
				}
			}
		}

	}

	public static void setAllKnowledge(String username)
	{
		if (!TOME_KNOWLEDGE.contains(username))
		{
			TOME_KNOWLEDGE.add(username);

			MAP.remove(username);

			IOHandler.markDirty();
		}
	}

	public static void setKnowledge(String username, LinkedList<ItemStack> list)
	{
		if (!TOME_KNOWLEDGE.contains(username))
		{
			MAP.put(username, list);

			IOHandler.markDirty();
		}
	}

	public static boolean hasKnowledgeForStack(EntityPlayer player, ItemStack stack)
	{
		for (ItemStack s : Transmutation.getKnowledge(player.getCommandSenderName()))
		{
			if (s == null)
			{
				continue;
			}

			if (stack.getItem().equals(s.getItem()) && stack.getItemDamage() == s.getItemDamage())
			{
				return true;
			}
		}

		return false;
	}

	public static boolean hasFullKnowledge(String username)
	{
		return TOME_KNOWLEDGE.contains(username);
	}
	
	public static void clearKnowledge(String username)
	{
		if (TOME_KNOWLEDGE.contains(username))
		{
			TOME_KNOWLEDGE.remove(username);
			
			IOHandler.markDirty();
		}
		
		if (MAP.containsKey(username))
		{
			MAP.remove(username);
			
			IOHandler.markDirty();
		}
	}
	
	public static double getStoredEmc(String username)
	{
		if (EMC_STORAGE.containsKey(username))
		{
			return EMC_STORAGE.get(username);
		}
		
		return 0;
	}
	
	public static void setStoredEmc(String username, double emc)
	{
		EMC_STORAGE.put(username, emc);
		IOHandler.markDirty();
	}
	
	public static void sync(EntityPlayer player)
	{
		NBTTagCompound tag = new NBTTagCompound();
		PETransmutation.getDataFor(player).saveNBTData(tag);
		PacketHandler.sendTo(new ClientKnowledgeSyncPKT(tag), (EntityPlayerMP) player);
	}
	
	public static void clear()
	{
		MAP.clear();
		TOME_KNOWLEDGE.clear();
		CACHED_TOME_KNOWLEDGE.clear();
		EMC_STORAGE.clear();
	}
	
	public static void loadFromNBT(NBTTagCompound playerKnowledge)
	{
		String player = playerKnowledge.getString("player");
		
		clearKnowledge(player);
		
		if (playerKnowledge.getBoolean("tome"))
		{
			cacheFullKnowledge();
			
			setAllKnowledge(player);
		}
		else
		{
			NBTTagList list = playerKnowledge.getTagList("data", NBT.TAG_COMPOUND);
			
			LinkedList<ItemStack> itemList = new LinkedList<ItemStack>(); 
			
			for (int i = 0; i < list.tagCount(); i++)
			{
				ItemStack stack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
				
				if (stack != null)
				{
					itemList.add(stack);
				}
			}
			
			setKnowledge(player, itemList);
		}
	}
	
	private static NBTTagCompound getPlayerNBT(String username)
	{
		NBTTagCompound knowledge = new NBTTagCompound();
		
		knowledge.setString("player", username);
		
		knowledge.setBoolean("tome", TOME_KNOWLEDGE.contains(username));
		
		NBTTagList list = new NBTTagList();
		
		for (ItemStack stack : getKnowledge(username))
		{
			if (stack != null)
			{
				NBTTagCompound nbt = new NBTTagCompound();
				stack.writeToNBT(nbt);
				list.appendTag(nbt);
			}
		}
		
		knowledge.setTag("data", list);
		
		return knowledge;
	}
	
	public static NBTTagCompound getAsNBT()
	{
		NBTTagCompound knowledge = new NBTTagCompound();
		
		NBTTagList tomeKnowledge = new NBTTagList();
		
		for (String username : TOME_KNOWLEDGE)
		{
			NBTTagCompound usernameNBT = new NBTTagCompound();
			usernameNBT.setString("player", username);
			tomeKnowledge.appendTag(usernameNBT);
		}
		
		knowledge.setTag("Tome Knowledge", tomeKnowledge);
		
		NBTTagList list = new NBTTagList();
		
		for (Entry<String, LinkedList<ItemStack>> entry : MAP.entrySet())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			
			nbt.setString("player", entry.getKey());
			
			NBTTagList items = new NBTTagList();
			
			for (ItemStack stack : entry.getValue())
			{
				if (stack != null)
				{
					NBTTagCompound itemNbt = new NBTTagCompound();
					stack.writeToNBT(itemNbt);
					items.appendTag(itemNbt);
				}
			}
			
			nbt.setTag("data", items);
			list.appendTag(nbt);
		}
		
		knowledge.setTag("knowledge", list);
		
		NBTTagList playerEMC = new NBTTagList();
		
		for (Entry<String, Double> entry : EMC_STORAGE.entrySet())
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("player", entry.getKey());
			nbt.setDouble("emc", entry.getValue());
			
			playerEMC.appendTag(nbt);
		}
		
		knowledge.setTag("playerEMC", playerEMC);
		
		return knowledge;
	}
}
