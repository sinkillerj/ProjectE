package moze_intel.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import moze_intel.MozeCore;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraftforge.common.util.Constants.NBT;

public class FileHelper 
{
	private static File worldDir;
	private static File knowledgeTxt;
	public static boolean saving = false;
	
	public static void loadKnowledge(LinkedHashMap<String, List<ItemStack>> map)
	{
		InputStream input = null;
		
		try
		{
			input = new FileInputStream(knowledgeTxt);
			
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(input);
			
			NBTTagList list = nbt.getTagList("Knowledge", 10);
			
			for (int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound subNbt = list.getCompoundTagAt(i);
				String username = subNbt.getString("Username");
				NBTTagList subList = subNbt.getTagList("Items", 10);
				List<ItemStack> knowledge = new ArrayList<ItemStack>();
				
				for (int j = 0; j < subList.tagCount(); j++)
				{
					knowledge.add(ItemStack.loadItemStackFromNBT(subList.getCompoundTagAt(j)));
				}
				
				map.put(username, knowledge);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			closeInStream(input);
		}
	}
	
	public static void saveKnowledge(LinkedHashMap<String, List<ItemStack>> map)
	{
		saving = true;
		
		OutputStream out = null;
		
		try 
		{
			out = new FileOutputStream(knowledgeTxt, false);
			
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList list = new NBTTagList();

			for (Entry<String, List<ItemStack>> entry : map.entrySet())
			{
				NBTTagCompound subNbt = new NBTTagCompound();
				subNbt.setString("Username", entry.getKey());
				
				NBTTagList subList = new NBTTagList();
				
				for (ItemStack stack : entry.getValue())
				{
					NBTTagCompound item = new NBTTagCompound();
					stack.writeToNBT(item);
					subList.appendTag(item);
				}
				
				subNbt.setTag("Items", subList);
				list.appendTag(subNbt);
			}
			
			nbt.setTag("Knowledge", list);
			
			CompressedStreamTools.writeCompressed(nbt, out);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally 
		{
			closeOutStream(out);
			saving = false;
		}
	}
	
	public static void getWorldDir()
	{
		MinecraftServer server = MinecraftServer.getServer();
		World world = server.getEntityWorld();
		
		String mcDir = server.getFile("").getPath();
		String worldName = world.getSaveHandler().getWorldDirectoryName();
		File worldSave = new File(mcDir, worldName);
		
		if (!worldSave.isDirectory())
		{
			MozeCore.logger.logInfo("Assuming SSP world, changing file directories.");
			File saveFolder = new File(mcDir, "saves");
			worldSave = new File(saveFolder, worldName);
			
			if (!worldSave.isDirectory())
			{
				MozeCore.logger.logFatal("Failed to load world save files!");
				return;
			}
		}
		
		worldDir = worldSave;
		MozeCore.logger.logInfo("Found world save: " + worldSave.getAbsolutePath());
		knowledgeTxt = new File(worldSave, "knowledge.dat");
	}
	
	private static void closeOutStream(OutputStream out)
	{
		try
		{
			if (out != null)
			{
				out.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static void closeInStream(InputStream in)
	{
		try
		{
			if (in != null)
			{
				in.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
