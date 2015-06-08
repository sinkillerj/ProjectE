package moze_intel.projecte.playerData;

import com.google.common.collect.Maps;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;

import java.util.Map;

public class PEAlchBags implements IExtendedEntityProperties
{
	public static final String PROP_NAME = "PEAlchBag";

	private final EntityPlayer thePlayer;
	private final Map<Byte, ItemStack[]> bagData = Maps.newHashMap();

	public static void register(EntityPlayer player)
	{
		player.registerExtendedProperties(PROP_NAME, new PEAlchBags(player));
	}

	public static PEAlchBags getDataFor(EntityPlayer player)
	{
		return ((PEAlchBags) player.getExtendedProperties(PROP_NAME));
	}

	public PEAlchBags(EntityPlayer player)
	{
		thePlayer = player;
	}

	public ItemStack[] getInv(byte color)
	{
		if (bagData.get(color) == null)
		{
			bagData.put(color, new ItemStack[104]);
			PELogger.logInfo("Created new inventory array for color " + color + " and player " + thePlayer.getCommandSenderName());
		}
		return bagData.get(color).clone();
	}

	public void setInv(byte color, ItemStack[] inv)
	{
		bagData.put(color, inv);
	}

	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = new NBTTagCompound();

		NBTTagList listOfInventories = new NBTTagList();
		for (int i = 0; i < 16; i++)
		{
			if (bagData.get(((byte) i)) == null)
			{
				continue;
			}
			NBTTagCompound inventory = new NBTTagCompound();
			inventory.setByte("color", ((byte) i));
			inventory.setTag("inv", ItemHelper.toNbtList(bagData.get(((byte) i))));
			listOfInventories.appendTag(inventory);
		}

		properties.setTag("data", listOfInventories);
		compound.setTag(PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = compound.getCompoundTag(PROP_NAME);
		NBTTagList listOfInventoies = properties.getTagList("data", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < listOfInventoies.tagCount(); i++)
		{
			NBTTagCompound inventory = listOfInventoies.getCompoundTagAt(i);
			bagData.put(inventory.getByte("color"), copyNBTToArray(inventory.getTagList("inv", Constants.NBT.TAG_COMPOUND)));
		}
	}

	private ItemStack[] copyNBTToArray(NBTTagList list)
	{
		ItemStack[] s = new ItemStack[104];
		for (int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound entry = list.getCompoundTagAt(i);
			s[entry.getByte("index")] = ItemStack.loadItemStackFromNBT(entry);
		}
		return s;
	}

	@Override
	public void init(Entity entity, World world) {}
}
