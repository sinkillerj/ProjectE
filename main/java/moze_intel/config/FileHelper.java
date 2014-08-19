package moze_intel.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import moze_intel.MozeCore;
import moze_intel.EMC.EMCMapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

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
				MozeCore.logger.logFatal("Exception in file I/O");
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
						MozeCore.logger.logFatal("Unexpected end of custom emc file.");
						break;
					}
					
					if (meta.discriminator != token.getNextDiscriminator())
					{
						MozeCore.logger.logFatal("Unexpected token in custom emc file.");
						continue;
					}
					
					ItemStack stack = null;
					
					try
					{
						 stack = getStackFromString(token.line, Integer.valueOf(meta.line));
					}
					catch (Exception e)
					{
						MozeCore.logger.logFatal("Syntax error in custom emc file.");
						e.printStackTrace();
						continue;
					}
					
					if (stack == null)
					{
						MozeCore.logger.logFatal("Couldn't get Item/Block from unlocalized name: "+token.line);
						continue;
					}
					
					Token emc = getNextToken(reader);
					
					if (emc == null)
					{
						MozeCore.logger.logFatal("Unexpected end of custom emc file.");
						break;
					}
					
					if (emc.discriminator != meta.getNextDiscriminator())
					{
						MozeCore.logger.logFatal("Unexpected token in custom emc file.");
						continue;
					}
					
					int emcValue;
					
					try
					{
						emcValue = Integer.valueOf(emc.line);
					}
					catch (Exception e)
					{
						MozeCore.logger.logFatal("Syntax error in custom emc file.");
						e.printStackTrace();
						continue;
					}
					
					EMCMapper.addMapping(stack, emcValue);
					MozeCore.logger.logInfo(("Register custom EMC value ("+emcValue+") for: "+token.line));
				}
				else if (token.discriminator == 'O')
				{
					String oreName = token.line;
					List<ItemStack> oreList = OreDictionary.getOres(oreName);
					
					if (oreList.isEmpty())
					{
						MozeCore.logger.logFatal(("Ore-Dictionary search for "+oreName+" in custom emc file returned nothing."));
						continue;
					}
					
					Token emc = getNextToken(reader);
					
					if (emc.discriminator != token.getNextDiscriminator())
					{
						MozeCore.logger.logFatal("Unexpected token in custom emc file.");
						continue;
					}
					
					int emcValue;
					
					try
					{
						emcValue = Integer.valueOf(emc.line);
					}
					catch (Exception e)
					{
						MozeCore.logger.logFatal("Syntax error in custom emc file.");
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
					
					MozeCore.logger.logInfo("Registered custom EMC value ("+emcValue+") for: "+oreName);
				}
			}
		}
		catch(Exception e)
		{
			MozeCore.logger.logFatal("Caught exception in custom emc file handling!");
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
	
	/**
	 *	@throws NullPointerException 
	 */
	private static ItemStack getStackFromString(String unlocalName, int metaData)
	{
		Iterator<String> iter = Item.itemRegistry.getKeys().iterator();
		
		while (iter.hasNext())
		{
			String obj = iter.next();

			ItemStack stack = new ItemStack((Item) Item.itemRegistry.getObject(obj), 1, metaData);
			
			try
			{
				if (stack.getUnlocalizedName() == null)
				{
					MozeCore.logger.logInfo("NULL unlocalized name for: "+stack);
					continue;
				}
			
				if (stack.getUnlocalizedName().equalsIgnoreCase(unlocalName))
				{
					return stack;
				}
			}
			catch (Exception e) {}
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
