package moze_intel.network.commands;

import moze_intel.MozeCore;
import moze_intel.network.packets.AddEmcPKT;
import moze_intel.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.OreDictionary;

public class RemoveEmcCMD extends ProjectEBaseCMD
{
	@Override
	public String getCommandName() 
	{
		return "projecte_removeEMC";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/projecte_removeEMC <UN/OD> <Actual unlocalized/ore-dictionary name> <Metada (ONLY with UN registrations!)";
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
			
			MozeCore.pktHandler.sendToServer(new AddEmcPKT(stack, 0));
		}
		else if (type.equals("OD"))
		{
			String odName = params[1];
			
			if (OreDictionary.getOres(odName).isEmpty())
			{
				sendError(sender, "Error: no entries for the OD name: " + odName);
				return;
			}
			
			MozeCore.pktHandler.sendToServer(new AddEmcPKT(odName, 0));
		}
		else
		{
			
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
