package moze_intel.projecte.network.commands;

import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class AddEmcCMD extends ProjectEBaseCMD 
{
	@Override
	public String getCommandName() 
	{
		return "projecte_addEMC";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/projecte_addEMC <UN/OD> <the actual unlocalized-name/ore dictionary name> <metadata (ONLY with Unlocalized names!> <EMC value>";
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		if (params.length < 2)
		{
			sendError(sender, "Error: command needs parameters!");
			return;
		}
		
		String type = params[0];
		
		if (type.equalsIgnoreCase("UN"))
		{
			if (params.length < 4)
			{
				sendError(sender, "Error: not enough parameters! UN registration requires the unlocalized name, the meta-data and the EMC value!");
				return;
			}
			
			String unlocalName = params[1];
			
			int meta;
			
			try
			{
				meta = Integer.valueOf(params[2]);
			}
			catch (Exception e)
			{
				sendError(sender, "Error: the metadata needs to be a number!");
				return;
			}
			
			ItemStack stack;
			
			try
			{
				stack = Utils.getStackFromString(unlocalName, meta);
			}
			catch (Exception e)
			{
				sendError(sender, "Error: couldn't find any item/block with unlocalized-name: " + unlocalName);
				e.printStackTrace();
				return;
			}
			
			if (stack != null)
			{
				int emc;
				
				try
				{
					emc = Integer.valueOf(params[3]);
				}
				catch (Exception e)
				{
					sendError(sender, "Error: the EMC value needs to be a number!");
					return;
				}
				
				if (EMCMapper.addCustomEntry(stack, emc))
				{
					sendSuccess(sender, "Added EMC (" + emc + ") to " + unlocalName);
				}
				else
				{
					sendError(sender, "An error occured during the operation.");
				}
			}
			else
			{
				sendError(sender, "Error: couldn't find any item/block with unlocalized-name: " + unlocalName);
			}
		}
		else if (type.equalsIgnoreCase("OD"))
		{
			if (params.length < 3)
			{
				sendError(sender, "Error: not enough parameters UN registration requires the ore-dictionary name and the EMC value!");
				return;
			}
			
			String odName = params[1];
			
			if (OreDictionary.getOres(odName).isEmpty())
			{
				sendError(sender, "Error: no entries for the OD name: " + odName);
				return;
			}
			
			int emc;
			
			try
			{
				emc = Integer.valueOf(params[2]);
			}
			catch (Exception e)
			{
				sendError(sender, "Error: the EMC value needs to be a number!");
				return;
			}
			
			if (EMCMapper.addCustomEntry(odName, emc))
			{
				sendSuccess(sender, "Added EMC (" + emc + ") to " + odName);
			}
			else
			{
				sendError(sender, "An error occured during the operation.");
			}
		}
		else
		{
			sendError(sender, "Error: " + type + "is invalid! The type must either be UN or OD!");
		}
	}
}
