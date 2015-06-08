package moze_intel.projecte.playerData;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moze_intel.projecte.utils.EMCHelper;
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
import java.util.Map;

public class PETransmutation implements IExtendedEntityProperties
{
	private final EntityPlayer thePlayer;

	private double transmutationEmc;
	private List<ItemStack> knowledge = Lists.newArrayList();
	private boolean hasFullKnowledge;
	private Map<Byte, ItemStack[]> bagData = Maps.newHashMap();

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
		thePlayer = player;
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
		compound.setTag(PROP_NAME, properties);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound)
	{
		NBTTagCompound properties = compound.getCompoundTag(PROP_NAME);

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
