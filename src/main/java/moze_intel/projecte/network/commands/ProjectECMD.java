package moze_intel.projecte.network.commands;

import java.util.*;
import net.minecraft.util.*;
import net.minecraft.command.*;
import moze_intel.projecte.emc.*;
import moze_intel.projecte.utils.*;
import moze_intel.projecte.config.*;
import moze_intel.projecte.network.*;
import net.minecraft.entity.player.*;
import moze_intel.projecte.playerData.*;
import moze_intel.projecte.network.packets.*;

public class ProjectECMD extends ProjectEBaseCMD
{
	public static final ArrayList<String> changelog = new ArrayList<>();
	
	public String getCommandName()
	{
		return "projecte";
	}
	
	public int getRequiredPermissionLevel()
	{
		return 0;
	}
	
	public String getCommandUsage(ICommandSender sender)
	{
		return "/projecte <log|clearKnowledge|reloadEMC|removeEMC|resetEMC|setEMC> [args]";
	}
	
	public void processCommand(ICommandSender sender, String[] params)
	{
		System.out.print("params: ");
		for(String s : params)
		{
			System.out.print(s + ",");
		}
		System.out.println("size=" + params.length);
		
		if(params.length < 1)
		{
			sendError(sender, "Usage: " + getCommandUsage(sender));
			return;
		}
		
		switch(params[0].toLowerCase(Locale.ENGLISH))
		{
			case "log":
				changelog(sender);
				break;
			case "clearknowledge":
				if(sender.canCommandSenderUseCommand(4, "projecte")) clearKnowledge(sender, Utils.copy(params, 1, params.length));
				else insufficientPermission(sender);
				break;
			case "reloademc":
				if(sender.canCommandSenderUseCommand(4, "projecte")) reloadEMC(sender);
				else insufficientPermission(sender);
				break;
			case "removeemc":
				if(sender.canCommandSenderUseCommand(4, "projecte")) removeEMC(sender, Utils.copy(params, 1, params.length));
				else insufficientPermission(sender);
				break;
			case "resetemc":
				if(sender.canCommandSenderUseCommand(4, "projecte")) resetEMC(sender, Utils.copy(params, 1, params.length));
				else insufficientPermission(sender);
				break;
			case "setemc":
				if(sender.canCommandSenderUseCommand(4, "projecte")) setEMC(sender, Utils.copy(params, 1, params.length));
				else insufficientPermission(sender);
				break;
			default:
				sendError(sender, "Usage: " + getCommandUsage(sender));
				break;
		}
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}
	
	private void changelog(ICommandSender sender)
	{
		if(changelog.isEmpty())
		{
			sender.addChatMessage(new ChatComponentText("ProjectE is up to date."));
		}
		else
		{
			for(String s: changelog)
			{
				sender.addChatMessage(new ChatComponentText(s));
			}
		}
	}
	
	private void clearKnowledge(ICommandSender sender, String[] params)
	{
		if(params.length == 0)
		{
			if(sender instanceof EntityPlayerMP)
			{
				Transmutation.clearKnowledge(sender.getCommandSenderName());
				PacketHandler.sendTo(new ClientKnowledgeClearPKT(sender.getCommandSenderName()), (EntityPlayerMP)sender);
				sendSuccess(sender, "Cleared transmutation knowledge for: " + sender.getCommandSenderName());
			}
			else
			{
				sendError(sender, "Can't clear knowledge for " + sender.getCommandSenderName());
			}
		}
		else
		{
			for(Object obj : sender.getEntityWorld().playerEntities)
			{
				EntityPlayer player = (EntityPlayer)obj;
				
				if(player.getCommandSenderName().equalsIgnoreCase(params[0]))
				{
					Transmutation.clearKnowledge(player.getCommandSenderName());
					PacketHandler.sendTo(new ClientKnowledgeClearPKT(player.getCommandSenderName()), (EntityPlayerMP)player);
					sendSuccess(sender, "Cleared transmutation knowledge for: " + player.getCommandSenderName());
					
					if(!player.getCommandSenderName().equals(sender.getCommandSenderName()))
					{
						player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Your transmutation knowledge was cleared by " + sender.getCommandSenderName() + "!"));
					}
					
					return;
				}
			}
			
			sendError(sender, "Couldn't find player named: " + params[0]);
		}
	}
	
	private void reloadEMC(ICommandSender sender)
	{
		sender.addChatMessage(new ChatComponentText("[ProjectE] Reloading EMC registrations..."));
		
		EMCMapper.clearMaps();
		CustomEMCParser.readUserData();
		EMCMapper.map();
		
		sender.addChatMessage(new ChatComponentText("[ProjectE] Done! Sending updates to clients."));
		PacketHandler.sendToAll(new ClientSyncPKT());
	}
	
	private void removeEMC(ICommandSender sender, String[] params)
	{
		if(params.length < 1)
		{
			sendError(sender, "Usage: /projecte removeEMC <unlocalized/ore dictionary name> [metadata]");
			return;
		}
		
        String name = params[0];
        int meta = 0;
        
        if(name.contains(":") && params.length > 1)
        {
            try
            {
                meta = Integer.valueOf(params[1]);
            }
            catch (NumberFormatException e)
            {
                sendError(sender, "Error: the metadata passed (" + params[1] + ") is not a number!");
                return;
            }
            
            if(meta < 0)
            {
                sendError(sender, "Error: the metadata needs to be greater than or equal to 0!");
                return;
            }
        }
        
        if(CustomEMCParser.addToFile(name, meta, 0))
        {
            EMCMapper.clearMaps();
            CustomEMCParser.readUserData();
            EMCMapper.map();
            PacketHandler.sendToAll(new ClientSyncPKT());

            sendSuccess(sender, "Removed EMC value for: " + name);
        }
        else
        {
            sendError(sender, "Error: couldn't find any valid items for: " + name);
        }
	}
	
	private void resetEMC(ICommandSender sender, String[] params)
	{
		if(params.length < 1)
		{
			sendError(sender, "Usage: /projecte resetEMC <unlocalized/ore dictionary name> [metadata]");
			return;
		}
		
        String name = params[0];
        int meta = 0;
        
        if(name.contains(":") && params.length > 1)
        {
            try
            {
                meta = Integer.valueOf(params[1]);
            }
            catch (NumberFormatException e)
            {
                sendError(sender, "Error: the metadata passed (" + params[1] + ") is not a number!");
                return;
            }
            
            if(meta < 0)
            {
                sendError(sender, "Error: the metadata needs to be greater than or equal to 0!");
                return;
            }
        }
        
        if(CustomEMCParser.removeFromFile(name, meta))
        {
            EMCMapper.clearMaps();
            CustomEMCParser.readUserData();
            EMCMapper.map();
            PacketHandler.sendToAll(new ClientSyncPKT());

            sendSuccess(sender, "Reset EMC value for: " + name);
        }
        else
        {
            sendError(sender, "The EMC for " + name + "," + meta + " has not been modified!");
        }
	}
	
	private void setEMC(ICommandSender sender, String[] params)
	{
		if(params.length < 2)
		{
			sendError(sender, "Usage: /projecte setEMC <unlocalized/ore dictionary name> [metadata] <EMC value>");
			return;
		}
		
        String name = params[0];
        int meta = 0;
        boolean isOD = !name.contains(":");
        
        if(!isOD && params.length > 2)
        {
            try
            {
                meta = Integer.valueOf(params[1]);
            }
            catch (NumberFormatException e)
            {
                sendError(sender, "Error: the metadata passed (" + params[1] + ") is not a number!");
                return;
            }
            
            if(meta < 0)
            {
                sendError(sender, "Error: the metadata needs to be greater than or equal to 0!");
                return;
            }
        }
        
        int emc = 0;
        
        if(isOD)
        {
            try
            {
                emc = Integer.valueOf(params[1]);
            }
            catch (NumberFormatException e)
            {
                sendError(sender, "Error: the EMC passed (" + params[1] + ") is not a number!");
                return;
            }
        }
        else
        {
            String sEmc;

            if(params.length > 2)
            {
                sEmc = params[2];
            }
            else
            {
                sEmc = params[1];
            }

            try
            {
                emc = Integer.valueOf(sEmc);
            }
            catch (NumberFormatException e)
            {
                sendError(sender, "Error: the EMC passed (" + sEmc + ") is not a number!");
                return;
            }
        }

        if(emc <= 0)
        {
            sendError(sender, "Error: the EMC value needs to be greater than 0!");
            return;
        }
        
        if(CustomEMCParser.addToFile(name, meta, emc))
        {
            EMCMapper.clearMaps();
            CustomEMCParser.readUserData();
            EMCMapper.map();
            PacketHandler.sendToAll(new ClientSyncPKT());

            sendSuccess(sender, "Registered EMC value for: " + name + "(" + emc + ")");
        }
        else
        {
            sendError(sender, "Error: couldn't find any valid items for: " + name);
        }
	}
	
	private void insufficientPermission(ICommandSender sender)
	{
		ChatComponentTranslation component = new ChatComponentTranslation("commands.generic.permission");
		component.getChatStyle().setColor(EnumChatFormatting.RED);
		sender.addChatMessage(component);
	}
}
