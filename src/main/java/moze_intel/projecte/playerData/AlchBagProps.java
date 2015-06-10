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

public class AlchBagProps implements IExtendedEntityProperties
{
	public static final String PROP_NAME = "ProjectEAlchBag";

	private final EntityPlayer player;
	private final Map<Integer, ItemStack[]> bagData = Maps.newHashMap();
	private boolean hasMigrated = false;

	public static void register(EntityPlayer player)
	{
		player.registerExtendedProperties(PROP_NAME, new AlchBagProps(player));
	}

	public static AlchBagProps getDataFor(EntityPlayer player)
	{
		return ((AlchBagProps) player.getExtendedProperties(PROP_NAME));
	}

	public AlchBagProps(EntityPlayer player)
	{
		this.player = player;
	}

	protected ItemStack[] getInv(int color)
	{
		if (bagData.get(color) == null)
		{
			bagData.put(color, new ItemStack[104]);
			PELogger.logInfo("Created new inventory array for color " + color + " and player " + player.getCommandSenderName());
		}
		return bagData.get(color).clone();
	}

	protected void setInv(int color, ItemStack[] inv)
	{
		bagData.put(color, inv);
	}

	protected void saveForPacket(NBTTagCompound compound)
	{
		NBTTagList listOfInventories = new NBTTagList();
		for (int i = 0; i < 16; i++)
		{
			if (bagData.get(i) == null)
			{
				continue;
			}
			NBTTagCompound inventory = new NBTTagCompound();
			inventory.setInteger("color", i);
			inventory.setTag("inv", ItemHelper.toNbtList(bagData.get(i)));
			listOfInventories.appendTag(inventory);
		}
		compound.setTag("data", listOfInventories);
	}
	/**
	 * Only write one bag's data. Used for partial sync packets
	 */
	protected void saveForPartialPacket(NBTTagCompound compound, int color)
	{
		NBTTagList listOfInventories = new NBTTagList();
		if (bagData.get(color) == null)
		{
			return;
		}
		NBTTagCompound inventory = new NBTTagCompound();
		inventory.setInteger("color", color);
		inventory.setTag("inv", ItemHelper.toNbtList(bagData.get(color)));
		listOfInventories.appendTag(inventory);
		compound.setTag("data", listOfInventories);
	}

	public void readFromPacket(NBTTagCompound compound)
	{
		NBTTagList listOfInventoies = compound.getTagList("data", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < listOfInventoies.tagCount(); i++)
		{
			NBTTagCompound inventory = listOfInventoies.getCompoundTagAt(i);
			bagData.put(inventory.getInteger("color"), copyNBTToArray(inventory.getTagList("inv", Constants.NBT.TAG_COMPOUND)));
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
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = new NBTTagCompound();

		NBTTagList listOfInventories = new NBTTagList();
		for (int i = 0; i < 16; i++)
		{
			if (bagData.get(i) == null)
			{
				continue;
			}
			NBTTagCompound inventory = new NBTTagCompound();
			inventory.setInteger("color", i);
			inventory.setTag("inv", ItemHelper.toNbtList(bagData.get(i)));
			listOfInventories.appendTag(inventory);
		}

		properties.setTag("data", listOfInventories);
		properties.setBoolean("migrated", hasMigrated);
		compound.setTag(PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = compound.getCompoundTag(PROP_NAME);

		NBTTagList listOfInventoies = properties.getTagList("data", Constants.NBT.TAG_COMPOUND);
		hasMigrated = properties.getBoolean("migrated");
		if (!hasMigrated && !player.worldObj.isRemote) // hasMigrated is also false if tag was not present
		{
			if (AlchemicalBags.hasLegacyData(player))
			{
				listOfInventoies = AlchemicalBags.migratePlayerData(player);
				PELogger.logInfo("Migrated data for player: " + player.getCommandSenderName());
			}
			hasMigrated = true;
		}
		for (int i = 0; i < listOfInventoies.tagCount(); i++)
		{
			NBTTagCompound inventory = listOfInventoies.getCompoundTagAt(i);
			bagData.put(inventory.getInteger("color"), copyNBTToArray(inventory.getTagList("inv", Constants.NBT.TAG_COMPOUND)));
		}
	}

	@Override
	public void init(Entity entity, World world) {}
}
