package moze_intel.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import moze_intel.MozeCore;
import moze_intel.EMC.EMCMapper;
import moze_intel.utils.PELogger;
import moze_intel.utils.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class FileHelper 
{
	private static File EMC_CONFIG;
	private static final char[] TOKENS = new char[] {'U', 'O', 'M', 'E'};
	
	public static void init()
	{
		EMC_CONFIG = new File(MozeCore.CONFIG_DIR, "custom_emc.cfg");
		
		if (!EMC_CONFIG.exists())
		{
			try
			{
				EMC_CONFIG.createNewFile();
			}
			catch (IOException e)
			{
				PELogger.logFatal("Exception in file I/O");
				e.printStackTrace();
			}
			
			String[] tutorial = new String[]
			{
				"Custom EMC file",
				"In this file, you can register your own custom-emc values. You can use either unlocalized names or ore-dictionary entries.",
				"The syntax is really simple: each line must begin with a certain 'discriminator':",
				" -#- Anything following this will be completely ignored",
				" -U- Indicates you're using unlocalized names",
				" -O- Indicates you're using ore-dictionary entries",
				" -M- Is used only with unlocalized names for item/block metadata",
				" -E- Indicates the actual EMC value.",
				"",
				"Here are some examples (everything will start with # to avoid registration)",
				"",
				"# Unlocalized-name registration",
				"# U:item.appleGold",
				"# M:1",
				"# E:2048",
				"",
				"# Ore-Dictionary registration",
				"# O:itemRubber",
				"# E:32",
				"",
				"To find out unlocalized item names or Ore-Dictionary entries, check out Project-E's main config file."
			};
			
			PrintWriter writer = null;
			
			try
			{
				writer = new PrintWriter(EMC_CONFIG);
				
				for (String s : tutorial)
				{
					writer.println(s);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (writer != null)
				{
					try
					{
						writer.close();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void readUserData()
	{
		BufferedReader reader = null;
		
		try
		{
			reader = new BufferedReader(new FileReader(EMC_CONFIG));
			Token token;
			
			while ((token = getNextToken(reader)) != null)
			{
				if (token.discriminator == 'U')
				{
					Token meta = getNextToken(reader);
					
					if (meta == null)
					{
						PELogger.logFatal("Unexpected end of custom emc file.");
						break;
					}
					
					if (meta.discriminator != token.getNextDiscriminator())
					{
						PELogger.logFatal("Unexpected token in custom emc file.");
						continue;
					}
					
					ItemStack stack = null;
					
					try
					{
						 stack = Utils.getStackFromString(token.line, Integer.valueOf(meta.line));
					}
					catch (Exception e)
					{
						PELogger.logFatal("Syntax error in custom emc file.");
						e.printStackTrace();
						continue;
					}
					
					if (stack == null)
					{
						PELogger.logFatal("Couldn't get Item/Block from unlocalized name: "+token.line);
						continue;
					}
					
					Token emc = getNextToken(reader);
					
					if (emc == null)
					{
						PELogger.logFatal("Unexpected end of custom emc file.");
						break;
					}
					
					if (emc.discriminator != meta.getNextDiscriminator())
					{
						PELogger.logFatal("Unexpected token in custom emc file.");
						continue;
					}
					
					int emcValue;
					
					try
					{
						emcValue = Integer.valueOf(emc.line);
					}
					catch (Exception e)
					{
						PELogger.logFatal("Syntax error in custom emc file.");
						e.printStackTrace();
						continue;
					}
					
					EMCMapper.addMapping(stack, emcValue);
					PELogger.logInfo(("Register custom EMC value ("+emcValue+") for: "+token.line));
				}
				else if (token.discriminator == 'O')
				{
					String oreName = token.line;
					List<ItemStack> oreList = Utils.getODItems(oreName);
					
					if (oreList.isEmpty())
					{
						PELogger.logFatal(("Ore-Dictionary search for "+oreName+" in custom emc file returned nothing."));
						continue;
					}
					
					Token emc = getNextToken(reader);
					
					if (emc.discriminator != token.getNextDiscriminator())
					{
						PELogger.logFatal("Unexpected token in custom emc file.");
						continue;
					}
					
					int emcValue;
					
					try
					{
						emcValue = Integer.valueOf(emc.line);
					}
					catch (Exception e)
					{
						PELogger.logFatal("Syntax error in custom emc file.");
						continue;
					}
					
					for (ItemStack stack : oreList)
					{
						if (stack == null)
						{
							continue;
						}
						
						EMCMapper.addMapping(stack, emcValue);
					}
					
					PELogger.logInfo("Registered custom EMC value ("+emcValue+") for: "+oreName);
				}
			}
		}
		catch(Exception e)
		{
			PELogger.logFatal("Caught exception in custom emc file handling!");
			e.printStackTrace();
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * For UN additions only.
	 */
	public static boolean addToFile(ItemStack stack, int emc)
	{
		PrintWriter writer = null;
		BufferedReader reader = null;
		
		boolean hasFound = false;
		
		try
		{
			reader = new BufferedReader(new FileReader(EMC_CONFIG));

			String internal = Item.itemRegistry.getNameForObject(stack.getItem());
			String input = "";
			
			boolean foundStack = false;
			boolean foundMeta = false;

			Token token = null;
			LinkedList<String> list = new LinkedList();
			
			while ((token = getNextToken(reader, list)) != null)
			{
				if (token.discriminator == 'U' && token.line.equals(internal))
				{
					if (foundStack || foundMeta)
					{
						foundStack = false;
						foundMeta = false;
					}
					else
					{
						foundStack = true;
					}
				}
				else if (token.discriminator == 'M')
				{
					if (!foundStack || foundMeta)
					{
						foundStack = false;
						foundMeta = false;
					}
					else
					{
						int meta = -1;
						
						try
						{
							meta = Integer.valueOf(token.line);
						}
						catch (NumberFormatException e)
						{
							e.printStackTrace();
						}
						
						if (meta == stack.getItemDamage())
						{
							foundMeta = true;
						}
					}
				}
				else if (token.discriminator == 'E')
				{
					if (!foundStack || !foundMeta)
					{
						foundStack = false;
						foundMeta = false;
					}
					else
					{
						list.set(list.size() - 1, "E:" + emc);
						hasFound = true;
						foundStack = false;
						foundMeta = false;
					}
				}
				else
				{
					foundStack = false;
					foundMeta = false;
				}
				
				for (String s : list)
				{
					input += s + "\n";
				}
				
				list.clear();
			}
			
			if (!list.isEmpty())
			{
				for (String s : list)
				{
					input += s + "\n";
				}
				
				list.clear();
			}
			
			if (!hasFound)
			{
				input += "\n";
				input += "U:" + internal + "\n";
				input += "M:" + stack.getItemDamage() + "\n";
				input += "E:" + emc + "\n";
			}
			
			writer = new PrintWriter(new FileOutputStream(EMC_CONFIG, false));
			writer.write(input);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
			}
			
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}
	
	/**
	 * For OD additions only.
	 */
	public static boolean addToFile(String odName, int emc)
	{
		PrintWriter writer = null;
		BufferedReader reader = null;
		
		boolean hasFound = false;
		
		try
		{
			reader = new BufferedReader(new FileReader(EMC_CONFIG));
			
			String input = "";
			Token token = null;
			LinkedList<String> list = new LinkedList();
			
			boolean foundString = false;
			
			while ((token = getNextToken(reader, list)) != null)
			{
				if (token.discriminator == 'O')
				{
					if (foundString)
					{
						foundString = false;
					}
					else if (token.line.equals(odName))
					{
						foundString = true;
					}
				}
				else if (token.discriminator == 'E' && foundString)
				{
					list.set(list.size() - 1, "E:" + emc);
					hasFound = true;
					foundString = false;
				}
				else
				{
					foundString = false;
				}
				
				for (String s : list)
				{
					input += s + "\n";
				}
				
				list.clear();
			}
			
			if (!list.isEmpty())
			{
				for (String s : list)
				{
					input += s + "\n";
				}
				
				list.clear();
			}
			
			if (!hasFound)
			{
				input += "\n";
				input += "O:" + odName + "\n";
				input += "E:" + emc + "\n";
			}
			
			writer = new PrintWriter(new FileOutputStream(EMC_CONFIG, false));
			writer.write(input);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
			}
			
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return true;
	}
	
	/**
	 * For UN entries only
	 */
	public static boolean removeFromFile(ItemStack stack)
	{
		PrintWriter writer = null;
		BufferedReader reader = null;
		
		boolean hasFound = false;
		
		try
		{
			reader = new BufferedReader(new FileReader(EMC_CONFIG));
			
			String internal = Item.itemRegistry.getNameForObject(stack.getItem());
			String input = "";
			Token token = null;
			LinkedList<String> list = new LinkedList();
			
			boolean foundStack = false;
			boolean foundMeta = false;
			int stackIndex = 0;
			int metaIndex = 0;
			
			while ((token = getNextToken(reader, list)) != null)
			{
				if (token.discriminator == 'U' && token.line.equals(internal))
				{
					if (foundStack || foundMeta)
					{
						foundStack = false;
						foundMeta = false;
					}
					else
					{
						foundStack = true;
						stackIndex = list.size() - 1;
					}
				}
				else if (token.discriminator == 'M')
				{
					if (!foundStack || foundMeta)
					{
						foundStack = false;
						foundMeta = false;
					}
					else
					{
						int meta = -1;
						
						try
						{
							meta = Integer.valueOf(token.line);
						}
						catch (NumberFormatException e)
						{
							e.printStackTrace();
						}
						
						if (stack.getItemDamage() == meta)
						{
							foundMeta = true;
							metaIndex = list.size() - 1;
						}
					}
				}
				else if (token.discriminator == 'E')
				{
					if (!foundStack || !foundMeta)
					{
						foundStack = false;
						foundMeta = false;
					}
					else
					{
						if (stackIndex > 0 && list.get(stackIndex - 1).isEmpty())
						{
							list.remove(stackIndex - 1);
							stackIndex--;
							metaIndex--;
						}
						
						list.remove(stackIndex);
						list.remove(metaIndex);
						list.removeLast();
						hasFound = true;
						foundStack = false;
						foundMeta = false;
					}
				}
				else
				{
					foundStack = false;
					foundMeta = false;
				}
			}
			
			for (String s : list)
			{
				input += s + "\n";
			}
				
			list.clear();
			
			writer = new PrintWriter(new FileOutputStream(EMC_CONFIG, false));
			writer.write(input);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
			}
			
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return hasFound;
	}
	
	/**
	 * For OD entries only.
	 */
	public static boolean removeFromFile(String odName)
	{
		PrintWriter writer = null;
		BufferedReader reader = null;
		
		boolean hasFound = false;
		
		try
		{
			reader = new BufferedReader(new FileReader(EMC_CONFIG));
			
			String input = "";
			Token token = null;
			LinkedList<String> list = new LinkedList();
			
			boolean foundString = false;
			
			while ((token = getNextToken(reader, list)) != null)
			{
				if (token.discriminator == 'O')
				{
					if (token.line.equals(odName))
					{
						foundString = true;
						
						if (list.size() > 1 && list.get(list.size() - 2).isEmpty())
						{
							list.remove(list.size() - 2);
						}
						
						list.removeLast();
					}
				}
				else if (token.discriminator == 'E' && foundString)
				{
					list.removeLast();
					hasFound = true;
					foundString = false;
				}
				else
				{
					foundString = false;
				}
				
				for (String s : list)
				{
					input += s + "\n";
				}
				
				list.clear();
			}
			
			if (!list.isEmpty())
			{
				for (String s : list)
				{
					input += s + "\n";
				}
				
				list.clear();
			}
			
			writer = new PrintWriter(new FileOutputStream(EMC_CONFIG, false));
			writer.write(input);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (writer != null)
			{
				writer.close();
			}
			
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return hasFound;
	}
	
	private static Token getNextToken(BufferedReader reader, LinkedList<String> readLines) throws IOException
	{
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			readLines.add(line);
			
			line = line.trim();
			
			if (line.isEmpty())
			{
				continue;
			}
			
			char startChar = line.charAt(0);
			
			for (char c : TOKENS)
			{
				if (startChar == c && line.charAt(1) == ':')
				{
					return new Token(c, line.substring(2));
				}
			}
		}
		
		return null;
	}
	
	private static Token getNextToken(BufferedReader reader) throws IOException
	{
		String line;
		
		while ((line = reader.readLine()) != null)
		{
			
			line = line.trim();
			
			if (line.isEmpty())
			{
				continue;
			}
			
			char startChar = line.charAt(0);
			
			for (char c : TOKENS)
			{
				if (startChar == c && line.charAt(1) == ':')
				{
					return new Token(c, line.substring(2));
				}
			}
		}
		
		return null;
	}
	
	public static class Token
	{
		public char discriminator;
		public String line;
		
		public Token(char discriminator, String line)
		{
			this.discriminator = discriminator;
			this.line = line;
		}
		
		public char getNextDiscriminator()
		{
			switch (discriminator)
			{
				case 'U':
					return 'M';
				case 'O':
					return 'E';
				case 'M':
					return 'E';
				case 'E':
					return 'U';
			}
			
			return ' ';
		}
	}
}
