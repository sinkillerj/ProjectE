package moze_intel.playerData;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import moze_intel.MozeCore;
import moze_intel.EMC.EMCMapper;
import moze_intel.EMC.IStack;
import moze_intel.network.packets.ClientKnowledgeSyncPKT;
import moze_intel.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public abstract class TransmutationKnowledge 
{
	private static final LinkedHashMap<String, LinkedList<ItemStack>> MAP = new LinkedHashMap();
	private static final LinkedList<String> TOME_KNOWLEDGE = new LinkedList();
	private static final LinkedList<ItemStack> CACHED_TOME_KNOWLEDGE = new LinkedList();
	
	public static void loadCompleteKnowledge()
	{
		for (IStack stack : EMCMapper.emc.keySet())
		{
			try
			{
				CACHED_TOME_KNOWLEDGE.add(new ItemStack((Item) Item.itemRegistry.getObjectById(stack.id), 1, stack.damage));
			}
			catch (Exception e)
			{
				MozeCore.logger.logInfo("Failed to cache knowledge: " + e.toString());
				e.printStackTrace();
			}
		}
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
				if (!Utils.doesItemHaveEmc(list.get(i)))
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
	}
	
	public static void setAllKnowledge(String username)
	{
		if (!TOME_KNOWLEDGE.contains(username))
		{
			TOME_KNOWLEDGE.add(username);
			
			MAP.remove(username);
		}
	}
	
	public static void setKnowledge(String username, LinkedList<ItemStack> list)
	{
		if (!TOME_KNOWLEDGE.contains(username))
		{
			MAP.put(username, list);
		}
	}
	
	public static boolean hasFullKnowledge(String username)
	{
		return TOME_KNOWLEDGE.contains(username);
	}
	
	public static void sync(EntityPlayer player)
	{
		MozeCore.pktHandler.sendTo(new ClientKnowledgeSyncPKT(getPlayerNBT(player.getCommandSenderName())), (EntityPlayerMP) player);
	}
	
	public static void clear()
	{
		MAP.clear();
		TOME_KNOWLEDGE.clear();
		CACHED_TOME_KNOWLEDGE.clear();
	}
	
	public static void loadFromNBT(NBTTagCompound playerKnowledge)
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
		
		setKnowledge(playerKnowledge.getString("player"), itemList);
	}
	
	private static NBTTagCompound getPlayerNBT(String username)
	{
		NBTTagCompound knowledge = new NBTTagCompound();
		
		knowledge.setString("player", username);
		
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
		
		return knowledge;
	}
}
