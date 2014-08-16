package moze_intel.proxies;

import java.util.LinkedHashMap;

import net.minecraft.nbt.NBTTagCompound;

public class CommonProxy
{
	private static final LinkedHashMap<String, NBTTagCompound> PLAYER_DATA = new LinkedHashMap();
	
	public void registerKeyBinds() {} 
	public void registerRenderers() {}
	public void registerClientOnlyEvents() {}
	
	/**
	* Adds an entity's custom data to the map for temporary storage
	* @param compound An NBT Tag Compound that stores the IExtendedEntityProperties data only
	*/
	public static void storeEntityData(String name, NBTTagCompound compound)
	{
		PLAYER_DATA.put(name, compound);
	}

	/**
	* Removes the compound from the map and returns the NBT tag stored for name or null if none exists
	*/
	public static NBTTagCompound getEntityData(String name)
	{
		return PLAYER_DATA.remove(name);
	}
}
