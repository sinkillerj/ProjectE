package moze_intel.projecte.playerData;

import com.google.common.collect.Maps;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientSyncBagDataPKT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import moze_intel.projecte.utils.PELogger;

public final class AlchemicalBags 
{
	private static Map<String, Map<Byte, ItemStack[]>> MAP = Maps.newLinkedHashMap();
	
	public static ItemStack[] get(String player, byte bagColour)
	{
		Map<Byte, ItemStack[]> colorToInvMap;

		if (MAP.containsKey(player))
		{
			colorToInvMap = MAP.get(player);
			

			if (colorToInvMap == null) // Should not happen, diagnostic / debug code.
			{
				PELogger.logFatal(String.format("AlchemicalBags colorToInvMap returned null for player %s, retrying. Please send this log to the ProjectE developers.", player));
				colorToInvMap = MAP.get(player);
				if (colorToInvMap == null)
				{
					PELogger.logFatal(String.format("AlchemicalBags colorToInvMap retry failed for player %s. Please send this log to the ProjectE developers.", player));
					MAP.put(player, Maps.<Byte, ItemStack[]>newLinkedHashMap());
					MAP.get(player).put(bagColour, new ItemStack[104]);
					PELogger.logFatal(String.format("Now recreating inventory array for player %s and bagColour %s. Items may be wiped. Please report!!", player, Byte.toString(bagColour)));
					return MAP.get(player).get(bagColour).clone();
				}
			}
			
			if (!colorToInvMap.containsKey(bagColour))
			{
				PELogger.logDebug(String.format("Created inventory array for existing player %s, new bag color %s", player, Byte.toString(bagColour)));
				colorToInvMap.put(bagColour, new ItemStack[104]);
			}
			return colorToInvMap.get(bagColour).clone();
		}
		else
		{
			PELogger.logDebug(String.format("Created new maps for brand new player %s, new bag color %s", player, Byte.toString(bagColour)));
			MAP.put(player, Maps.<Byte, ItemStack[]>newLinkedHashMap());
			MAP.get(player).put(bagColour, new ItemStack[104]);
			return MAP.get(player).get(bagColour).clone();
		}
	}
	
	public static void set(String player, byte bagColour, ItemStack[] inv)
	{
		Map<Byte, ItemStack[]> setdata;

		if (MAP.containsKey(player))
		{
			setdata = MAP.get(player);
		}
		else
		{
			setdata = Maps.newLinkedHashMap();
			MAP.put(player, setdata);
		}
		if (setdata == null)
		{
			setdata = Maps.newLinkedHashMap();
			MAP.put(player, setdata);
		}
		setdata.put(bagColour, inv);
		
		IOHandler.markDirty();
	}
	
	public static void sync(EntityPlayer player)
	{
		PacketHandler.sendTo(new ClientSyncBagDataPKT(getPlayerNBT(player.getName())), (EntityPlayerMP) player);
	}
	
	public static void clear()
	{
		MAP.clear();
	}
	
	public static void loadFromNBT(NBTTagCompound nbt)
	{
		NBTTagList list = nbt.getTagList("data", NBT.TAG_COMPOUND);
		
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound subNbt = list.getCompoundTagAt(i);
			
			ItemStack[] inv = new ItemStack[104];
			
			NBTTagList subList = subNbt.getTagList("inv", NBT.TAG_COMPOUND);
			
			for (int j = 0; j < subList.tagCount(); j++)
			{
				NBTTagCompound subNbt3 = subList.getCompoundTagAt(j);
				inv[subNbt3.getByte("index")] = ItemStack.loadItemStackFromNBT(subNbt3);
			}
			
			set(nbt.getString("player"), subNbt.getByte("color"), inv);
		}
	}
	
	private static NBTTagCompound getPlayerNBT(String player)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		
		nbt.setString("player", player);
		
		if (MAP.containsKey(player))
		{
			Map<Byte, ItemStack[]> data = MAP.get(player);
			
			NBTTagList list = new NBTTagList();
			
			for (Entry<Byte, ItemStack[]> entry : data.entrySet())
			{
				NBTTagCompound subNbt = new NBTTagCompound();
				subNbt.setByte("color", entry.getKey());
				
				NBTTagList subList = new NBTTagList();
				
				ItemStack[] inv = entry.getValue();
				
				for (int i = 0; i < inv.length; i++)
				{
					ItemStack stack = inv[i];
					
					if (stack != null)
					{
						NBTTagCompound subNbt2 = new NBTTagCompound();
						subNbt2.setByte("index", (byte) i);
						stack.writeToNBT(subNbt2);
						subList.appendTag(subNbt2);
					}
				}
				
				subNbt.setTag("inv", subList);
				list.appendTag(subNbt);
			}
			
			nbt.setTag("data", list);
		}
		
		return nbt;
	}
	
	public static NBTTagCompound getAsNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		
		NBTTagList list = new NBTTagList();
		
		for (Entry<String, Map<Byte, ItemStack[]>> entry : MAP.entrySet())
		{
			NBTTagCompound subNBT = new NBTTagCompound();
			subNBT.setString("player", entry.getKey());
			
			NBTTagList subList = new NBTTagList();
			
			for (Entry<Byte, ItemStack[]> entry2 : entry.getValue().entrySet())
			{
				NBTTagCompound subNBT2 = new NBTTagCompound();
				subNBT2.setByte("color", entry2.getKey());
				
				NBTTagList subList2 = new NBTTagList();
				
				ItemStack[] inv = entry2.getValue();
				
				for (int i = 0; i < inv.length; i++)
				{
					ItemStack stack = inv[i];
					
					if (stack != null)
					{
						NBTTagCompound subNBT3 = new NBTTagCompound();
						subNBT3.setByte("index", (byte) i);
						stack.writeToNBT(subNBT3);
						subList2.appendTag(subNBT3);
					}
				}
				
				subNBT2.setTag("inv", subList2);
				subList.appendTag(subNBT2);
			}
			
			subNBT.setTag("data", subList);
			list.appendTag(subNBT);
		}
		
		nbt.setTag("bagdata", list);
		
		return nbt;
	}
}
