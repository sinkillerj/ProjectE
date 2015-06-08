package moze_intel.projecte.playerData;

import com.google.common.collect.Lists;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants;

import java.util.Iterator;
import java.util.List;

public class PETransmutation implements IExtendedEntityProperties
{
	private final EntityPlayer player;

	private double transmutationEmc;
	private List<ItemStack> knowledge = Lists.newArrayList();
	private boolean hasFullKnowledge;
	private boolean hasMigrated = false;

	public static final String PROP_NAME = "PETransmutation";

	public static void register(EntityPlayer player)
	{
		player.registerExtendedProperties(PROP_NAME, new PETransmutation(player));
	}

	public static PETransmutation getDataFor(EntityPlayer player)
	{
		return ((PETransmutation) player.getExtendedProperties(PROP_NAME));
	}

	public PETransmutation(EntityPlayer player)
	{
		this.player = player;
	}

	public boolean hasFullKnowledge()
	{
		return hasFullKnowledge;
	}

	public double getTransmutationEmc()
	{
		return transmutationEmc;
	}

	public void setTransmutationEmc(double transmutationEmc)
	{
		this.transmutationEmc = transmutationEmc;
	}

	public List<ItemStack> getKnowledge()
	{
		pruneStaleKnowledge();
		return knowledge;
	}

	private void pruneStaleKnowledge()
	{
		Iterator<ItemStack> iter = knowledge.iterator();
		while (iter.hasNext())
		{
			if (!EMCHelper.doesItemHaveEmc(iter.next()))
			{
				iter.remove();
			}
		}
	}

	@Override
	public void saveNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = new NBTTagCompound();
		properties.setDouble("transmutationEmc", transmutationEmc);
		properties.setBoolean("tome", hasFullKnowledge);

		pruneStaleKnowledge();
		NBTTagList knowledgeWrite = new NBTTagList();
		for (ItemStack i : knowledge)
		{
			NBTTagCompound tag = i.writeToNBT(new NBTTagCompound());
			knowledgeWrite.appendTag(tag);
		}
		properties.setTag("knowledge", knowledgeWrite);
		properties.setBoolean("migrated", hasMigrated);
		compound.setTag(PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = compound.getCompoundTag(PROP_NAME);
		hasMigrated = properties.getBoolean("migrated");
		if (!hasMigrated && !player.worldObj.isRemote)
		{
			properties = Transmutation.migratePlayerData(player);
			PELogger.logInfo("Migrated transmutation data for player: " + player.getCommandSenderName());
			hasMigrated = true;
		}

		transmutationEmc = properties.getDouble("transmutationEmc");
		hasFullKnowledge = properties.getBoolean("tome");

		NBTTagList list = properties.getTagList("knowledge", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++)
		{
			ItemStack item = ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i));
			if (item != null)
			{
				knowledge.add(item);
			}
		}
	}

	@Override
	public void init(Entity entity, World world)
	{

	}

	public void setFullKnowledge(boolean fullKnowledge)
	{
		this.hasFullKnowledge = fullKnowledge;
	}
}
