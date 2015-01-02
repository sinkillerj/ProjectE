package moze_intel.projecte.utils;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class IMCHandler 
{
	public static void handleIMC(IMCMessage msg)
	{
		if (msg.key.equalsIgnoreCase("registeremc"))
		{
			if (msg.isNBTMessage())
			{
				NBTTagCompound nbt = msg.getNBTValue();
				
				if (nbt == null)
				{
					PELogger.logFatal("Parsed null NBT tag in register EMC IMC message from mod: " + msg.getSender());
					return;
				}
				
				ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
				
				if (stack == null)
				{
					PELogger.logFatal("Couldn't parse ItemStack from NBT IMC message from mod: " + msg.getSender());
					PELogger.logFatal("NBT data: " + nbt);
					return;
				}
				
				if (!nbt.hasKey("EMC"))
				{
					PELogger.logFatal("No EMC value parsed in register EMC IMC message from mod: " + msg.getSender());
					return;
				}
				
				int emc = nbt.getInteger("EMC");
				
				if (emc < 0)
				{
					emc = 0;
				}
				
				if (EMCMapper.addIMCRegistration(stack, emc))
				{
					PELogger.logInfo("Mod "+ msg.getSender() +" registered and EMC value from IMC message (" + stack + "," + emc + ").");
				}
				else
				{
					PELogger.logFatal("Failed to register EMC value from IMC message for: " + stack);
					PELogger.logFatal("The ItemStack has probably already been registered.");
				}
			}
			else
			{
				PELogger.logFatal("Incorrect IMC message from mod: " + msg.getSender());
				PELogger.logFatal("Excpected an NBT IMC message for EMC registration, got: " + msg.getMessageType());
			}
		}
		else if (msg.key.equalsIgnoreCase("interdictionblacklist"))
		{
			if (msg.isStringMessage())
			{
				String s = msg.getStringValue().trim();

				try 
				{
					Class c = Class.forName(s);
					
					if (InterdictionTile.addEntityToBlackList(c))
					{
						PELogger.logInfo("Mod " + msg.getSender() + " blacklisted the entity " + c.getCanonicalName() + " for interdiction torches.");
					}
					else
					{
						PELogger.logFatal("Failed to black list the entity " + c.getCanonicalName() + " for interdiction torches.");
						PELogger.logFatal("The entity has probably already been whitelisted.");
					}
				} 
				catch (ClassNotFoundException e) 
				{
					PELogger.logFatal("Error in IMC message: couldn't find class for: " + s);
				}
			}
		}
		else if (msg.key.equalsIgnoreCase("nbtwhitelist"))
		{
			if (msg.isItemStackMessage())
			{
				ItemStack stack = msg.getItemStackValue();

				if (stack == null)
				{
					PELogger.logFatal("Parsed null ItemStack in NBT whitelist IMC message from mod: " + msg.getSender());
					return;
				}

				NBTWhitelist.register(stack);

				PELogger.logInfo("Mod " + msg.getSender() + " registered NBT whitelist for ItemStack: " + stack.getUnlocalizedName());
			}
		}
		else
		{
			PELogger.logFatal("Incorrect IMC message key from mod " + msg.getSender() + ": " + msg.key);
		}
	}
}
