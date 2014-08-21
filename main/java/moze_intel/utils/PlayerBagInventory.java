package moze_intel.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import scala.actors.threadpool.Arrays;
import moze_intel.MozeCore;
import moze_intel.network.packets.ClientKnowledgeSyncPKT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;

public class PlayerBagInventory implements IExtendedEntityProperties 
{
	public static final String EXT_PROP_NAME = "ALCHEMY_BAG_DATA";
	private LinkedHashMap<Integer, ItemStack[]> data;
	private final EntityPlayer player;
	
	public PlayerBagInventory(EntityPlayer player)
	{
		this.player = player;
		data = new LinkedHashMap<Integer, ItemStack[]>();
	}
	
	public static void register(EntityPlayer player)
	{
		player.registerExtendedProperties(EXT_PROP_NAME, new PlayerBagInventory(player));
	}
	
	public static PlayerBagInventory getProperties(EntityPlayer player)
	{
		return (PlayerBagInventory) player.getExtendedProperties(EXT_PROP_NAME);
	}
	
	public static ItemStack[] getPlayerBagData(EntityPlayer player, int bagCoulour)
	{
		ItemStack inv[] = getProperties(player).data.get(bagCoulour);
		
		if (inv == null)
		{
			return new ItemStack[104];
		}
		
		return inv;
	}
	
	public static void setPlayerBagData(EntityPlayer player, int bagColour, ItemStack[] inv)
	{
		getProperties(player).data.put(bagColour, inv);
	}
	
	/**
	 * Make sure to call this ONLY on the server side!
	 * @param player
	 */
	public static void syncPlayerProps(EntityPlayer player)
	{
		PlayerBagInventory props = getProperties(player);
		
		if (props == null)
		{
			MozeCore.logger.logFatal("Unregistered player knowledge! Please report to mod dev!");
		}
		else
		{
			NBTTagCompound nbt = new NBTTagCompound();
			props.saveNBTData(nbt);
			
			MozeCore.pktHandler.sendTo(new ClientKnowledgeSyncPKT(nbt), (EntityPlayerMP) player);
		}
	}
	
	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound props = new NBTTagCompound();
		
		for (Entry<Integer, ItemStack[]> entry : data.entrySet())
		{
			NBTTagList nbtList = new NBTTagList();
			
			ItemStack[] inv = entry.getValue();
			
			if (inv != null)
			{
				for (int i = 0; i < inv.length; i++)
				{
					ItemStack stack = inv[i];
					
					if (stack != null)
					{
						NBTTagCompound itemNBT = new NBTTagCompound();
						itemNBT.setByte("Slot", (byte) i);
						stack.writeToNBT(itemNBT);
						nbtList.appendTag(itemNBT);
					}
				}
			}
			
			props.setTag(String.valueOf(entry.getKey()), nbtList);
		}
		
		compound.setTag(EXT_PROP_NAME, props);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) 
	{
		NBTTagCompound nbt = compound.getCompoundTag(EXT_PROP_NAME);
		
		for (int i = 0; i < 15; i++)
		{
			ItemStack[] inventory = new ItemStack[104];
			NBTTagList list = nbt.getTagList(String.valueOf(i), NBT.TAG_COMPOUND);
			
			for (int j = 0; j < list.tagCount(); j++)
			{
				NBTTagCompound item = list.getCompoundTagAt(j);
				byte slot = item.getByte("Slot");
				inventory[slot] = ItemStack.loadItemStackFromNBT(item);
			}
			
			data.put(i, inventory);
		}
	}
	
	@Override
	public void init(Entity entity, World world) 
	{
		//NOOP
	}
}
