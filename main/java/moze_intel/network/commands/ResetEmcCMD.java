package moze_intel.network.commands;

import moze_intel.MozeCore;
import moze_intel.EMC.EMCMapper;
import moze_intel.config.FileHelper;
import moze_intel.network.PacketHandler;
import moze_intel.network.packets.ClientSyncPKT;
import moze_intel.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ResetEmcCMD extends ProjectEBaseCMD
{
	@Override
	public String getCommandName() 
	{
		return "projecte_resetEMC";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/projecte_resetEMC <UN/OD> <Actual unlocalized/ore-dictionary name> <Metada (ONLY with UN registrations!)";
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
			if (params.length < 3)
			{
				sendError(sender, "Error: Error: not enough parameters! UN registration requires the unlocalized name and the meta-data!");
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
			
			
			if (FileHelper.removeFromFile(stack))
			{
				EMCMapper.clearMaps();
				FileHelper.readUserData();
				EMCMapper.map();
				PacketHandler.sendToAll(new ClientSyncPKT());
				
				sendSuccess(sender, "Reset EMC for " + unlocalName);
			}
			else
			{
				sendError(sender, "The EMC for " + unlocalName + " has not been modified!");
			}
		}
		else if (type.equals("OD"))
		{
			String odName = params[1];
			
			if (OreDictionary.getOres(odName).isEmpty())
			{
				sendError(sender, "Error: no entries for the OD name: " + odName);
				return;
			}
			
			
			if (FileHelper.removeFromFile(odName))
			{
				EMCMapper.clearMaps();
				FileHelper.readUserData();
				EMCMapper.map();
				PacketHandler.sendToAll(new ClientSyncPKT());
				
				sendSuccess(sender, "Reset EMC for " + odName);
			}
			else
			{
				sendError(sender, "The EMC for " + odName + " has not been modified!");
			}
		}
		else
		{
			sendError(sender, "Error: " + type + "is invalid! The type must either be UN or OD!");
		}
	}
}
