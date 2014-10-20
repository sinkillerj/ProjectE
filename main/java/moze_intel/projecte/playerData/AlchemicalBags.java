package moze_intel.projecte.playerData;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import cpw.mods.fml.common.FMLCommonHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientSyncBagDataPKT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;

public final class AlchemicalBags 
{
	private static LinkedHashMap<String, LinkedHashMap<Byte, ItemStack[]>> MAP = new LinkedHashMap();
	
	public static ItemStack[] get(String player, byte bagColour)
	{
		if (MAP.containsKey(player))
		{
			LinkedHashMap<Byte, ItemStack[]> data = MAP.get(player);
			
			if (data.containsKey(bagColour))
			{
				return data.get(bagColour).clone();
			}
		}
		
		return new ItemStack[104];
	}
	
	public static void set(String player, byte bagColour, ItemStack[] inv)
	{
		if (MAP.containsKey(player))
		{
			LinkedHashMap<Byte, ItemStack[]> data = MAP.get(player);
			data.put(bagColour, inv);
		}
		else
		{
			LinkedHashMap<Byte, ItemStack[]> data = new LinkedHashMap();
			data.put(bagColour, inv);
			MAP.put(player, data);
		}
	}
	
	public static void sync(EntityPlayer player)
	{
		PacketHandler.sendTo(new ClientSyncBagDataPKT(getPlayerNBT(player.getCommandSenderName())), (EntityPlayerMP) player);
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
			LinkedHashMap<Byte, ItemStack[]> data = MAP.get(player);
			
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
		
		for (Entry<String, LinkedHashMap<Byte, ItemStack[]>> entry : MAP.entrySet())
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
