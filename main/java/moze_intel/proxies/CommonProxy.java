package moze_intel.proxies;

import java.util.LinkedHashMap;

import net.minecraft.nbt.NBTTagCompound;

public class CommonProxy
{
	private static final LinkedHashMap<String, NBTTagCompound> PLAYER_DATA = new LinkedHashMap();
	
	public void registerKeyBinds() {} 
	public void registerRenderers() {}
	public void registerClientOnlyEvents() {}
	
	public static void storeEntityData(String name, NBTTagCompound compound)
	{
		PLAYER_DATA.put(name, compound);
	}

	public static NBTTagCompound getEntityData(String name)
	{
		return PLAYER_DATA.remove(name);
	}
	
	public static void clearAllData()
	{
		PLAYER_DATA.clear();
	}
}
