package moze_intel.utils;

import java.util.ArrayList;
import java.util.List;

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

public class PlayerKnowledge implements IExtendedEntityProperties
{
	public static final String EXT_PROP_NAME = "TRANSMUTATION_KNOWLEDGE";
	private List<ItemStack> knowledge;
	private final EntityPlayer player;
	
	public PlayerKnowledge(EntityPlayer player)
	{
		this.player = player;
		knowledge = new ArrayList();
	}
	
	public static void register(EntityPlayer player)
	{
		player.registerExtendedProperties(EXT_PROP_NAME, new PlayerKnowledge(player));
	}
	
	public static PlayerKnowledge getProperties(EntityPlayer player)
	{
		IExtendedEntityProperties properties = player.getExtendedProperties(EXT_PROP_NAME);
		
		if (properties == null)
		{
			register(player);
		}
		
		properties = player.getExtendedProperties(EXT_PROP_NAME);
		
		return (PlayerKnowledge) properties;
	}
	
	public static void addKnowledge(EntityPlayer player, ItemStack stack)
	{
		getProperties(player).knowledge.add(stack);
	}
	
	public static List<ItemStack> getPlayerKnowledge(EntityPlayer player)
	{
		return getProperties(player).knowledge;
	}
	
	/**
	 * Make sure to call this ONLY on the server side!
	 * @param player
	 */
	public static void syncPlayerProps(EntityPlayer player)
	{
		PlayerKnowledge props = getProperties(player);
		
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
		
		NBTTagList list = new NBTTagList();
		
		for (ItemStack stack : knowledge)
		{
			NBTTagCompound item = new NBTTagCompound();
			stack.writeToNBT(item);
			list.appendTag(item);
		}
		
		props.setTag("Knowledge", list);
		
		compound.setTag(EXT_PROP_NAME, props);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) 
	{
		NBTTagList list = compound.getCompoundTag(EXT_PROP_NAME).getTagList("Knowledge", NBT.TAG_COMPOUND);
		
		for (int i = 0; i < list.tagCount(); i++)
		{
			ItemStack stack = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
			
			if (Utils.doesItemHaveEmc(stack))
			{
				knowledge.add(stack);
			}
		}
	}
	
	@Override
	public void init(Entity entity, World world) 
	{
		//NOOP
	}
}
