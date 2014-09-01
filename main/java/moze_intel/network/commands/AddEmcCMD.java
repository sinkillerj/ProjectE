package moze_intel.network.commands;

import java.util.List;

import moze_intel.MozeCore;
import moze_intel.network.packets.AddEmcPKT;
import moze_intel.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
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
				
				MozeCore.pktHandler.sendToServer(new AddEmcPKT(stack, emc));
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
			
			MozeCore.pktHandler.sendToServer(new AddEmcPKT(odName, emc));
		}
		else
		{
			sendError(sender, "Error: " + type + "is invalid! The type must either be UN or OD!");
		}
	}
	
	private void sendError(ICommandSender sender, String message)
	{
		sendMessage(sender, EnumChatFormatting.RED + message);
	}
	
	private void sendMessage(ICommandSender sender, String message)
	{
		sender.addChatMessage(new ChatComponentText(message));
	}
}
