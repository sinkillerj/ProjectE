package moze_intel.proxies;

import java.util.LinkedHashMap;

import net.minecraft.nbt.NBTTagCompound;

public class CommonProxy
{
	private static final LinkedHashMap<String, NBTTagCompound> PLAYER_KNOWLEDGE_DATA = new LinkedHashMap();
	private static final LinkedHashMap<String, NBTTagCompound> PLAYER_BAG_DATA = new LinkedHashMap();
	
	public void registerKeyBinds() {} 
	public void registerRenderers() {}
	public void registerClientOnlyEvents() {}
	
	public static void storeEntityKnowleddge(String name, NBTTagCompound compound)
	{
		PLAYER_KNOWLEDGE_DATA.put(name, compound);
	}
	
	public static void storeEntityBagData(String name, NBTTagCompound compound)
	{
		PLAYER_BAG_DATA.put(name, compound);
	}

	public static NBTTagCompound getEntityKnowledge(String name)
	{
		return PLAYER_KNOWLEDGE_DATA.remove(name);
	}
	
	public static NBTTagCompound getEntityBagData(String name)
	{
		return PLAYER_BAG_DATA.remove(name);
	} 
	
	public static void clearAllKnowledge()
	{
		PLAYER_KNOWLEDGE_DATA.clear();
	}
	
	public static void clearAllBagData()
	{
		PLAYER_BAG_DATA.clear();
	}
}
