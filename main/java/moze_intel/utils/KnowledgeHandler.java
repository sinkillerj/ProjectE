package moze_intel.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class KnowledgeHandler 
{
	private static LinkedHashMap<String, List<ItemStack>> knowledge = new LinkedHashMap();
	
	public static void load()
	{
		FileHelper.loadKnowledge(knowledge);
	}
	
	public static void save()
	{
		FileHelper.saveKnowledge(knowledge);
	}
	
	public static void addPlayerKnowledge(String username , ItemStack stack)
	{
		List<ItemStack> list = knowledge.get(username);
		
		if (list == null)
		{
			list = new ArrayList<ItemStack>();
		}
		
		list.add(stack);
		
		knowledge.put(username, list);
	}

	public static List<ItemStack> getPlayerKnowledge(String username)
	{
		return knowledge.get(username);
	}
	
	public static void wipePlayerKnowledge(String username)
	{
		knowledge.remove(username);
	}
}