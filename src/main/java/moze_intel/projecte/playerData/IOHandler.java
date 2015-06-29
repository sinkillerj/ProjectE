package moze_intel.projecte.playerData;

import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public final class IOHandler
{
	private static File knowledgeFile;
	private static File bagDataFile;
	
	public static void init(File knowledge, File bagData)
	{
		if (!knowledge.exists() || !bagData.exists())
		{
			return;
		}
		
		knowledgeFile = knowledge;
		bagDataFile = bagData;
		
		readLegacyData();
	}
	
	private static void readLegacyData()
	{
		NBTTagCompound knowledge = null;

		try
		{
			knowledge = CompressedStreamTools.read(knowledgeFile);
		}
		catch (IOException e)
		{
			PELogger.logFatal("Error loading legacy knowledge file");
		}

		if (knowledge != null)
		{
			NBTTagList tomeKnowledge = knowledge.getTagList("Tome Knowledge", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < tomeKnowledge.tagCount(); i++)
			{
				NBTTagCompound tag = tomeKnowledge.getCompoundTagAt(i);

				String username = tag.getString("player");

				if (!username.isEmpty())
				{
					Transmutation.legacySetAllKnowledge(username);
				}
			}

			NBTTagList list = knowledge.getTagList("knowledge", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound subTag = list.getCompoundTagAt(i);

				LinkedList<ItemStack> stackList = new LinkedList<ItemStack>();

				NBTTagList subList = subTag.getTagList("data", Constants.NBT.TAG_COMPOUND);

				for (int j = 0; j < subList.tagCount(); j++)
				{
					ItemStack stack = ItemStack.loadItemStackFromNBT(subList.getCompoundTagAt(j));

					if (stack != null)
					{
						stackList.add(stack);
					}
				}

				Transmutation.legacySetKnowledge(subTag.getString("player"), stackList);
			}

			NBTTagList emc = knowledge.getTagList("playerEMC", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < emc.tagCount(); i++)
			{
				NBTTagCompound tag = emc.getCompoundTagAt(i);

				Transmutation.legacySetStoredEmc(tag.getString("player"), tag.getDouble("emc"));
			}
			PELogger.logDebug("** LOADED LEGACY TRANSMUTATION DATA **");
		}

		NBTTagCompound bagData = null;

		try
		{
			bagData = CompressedStreamTools.read(bagDataFile);
		}
		catch (Exception e)
		{
			PELogger.logFatal("Error loading legacy bag file");
		}

		if (bagData != null)
		{
			NBTTagList list = bagData.getTagList("bagdata", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound nbt = list.getCompoundTagAt(i);

				NBTTagList subList = nbt.getTagList("data", Constants.NBT.TAG_COMPOUND);

				for (int j = 0; j < subList.tagCount(); j++)
				{
					NBTTagCompound subNbt = subList.getCompoundTagAt(j);

					ItemStack[] inv = new ItemStack[104];

					NBTTagList subList2 = subNbt.getTagList("inv", Constants.NBT.TAG_COMPOUND);

					for (int k = 0; k < subList2.tagCount(); k++)
					{
						NBTTagCompound subNbt2 = subList2.getCompoundTagAt(k);

						inv[subNbt2.getByte("index")] = ItemStack.loadItemStackFromNBT(subNbt2);
					}

					AlchemicalBags.legacySet(nbt.getString("player"), subNbt.getByte("color"), inv);
				}
			}
			PELogger.logDebug("** LOADED LEGACY BAG DATA **");
		}
	}
}
