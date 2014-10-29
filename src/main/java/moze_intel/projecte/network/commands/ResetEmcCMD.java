package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.FileParser;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientSyncPKT;
import net.minecraft.command.ICommandSender;

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
		return "/projecte_resetEMC <unlocalized/ore-dictionary name> <metada (optional)>";
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		if (params.length < 1)
		{
			sendError(sender, "Error: command needs parameters!");
			return;
		}

        String name = params[0];
        int meta = 0;

        if (name.contains(":") && params.length > 1)
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

            if (meta < 0)
            {
                sendError(sender, "Error: the metadata needs to be grater or equal to 0!");
                return;
            }
        }

        if (FileParser.removeFromFile(name, meta))
        {
            EMCMapper.clearMaps();
            FileParser.readUserData();
            EMCMapper.map();
            PacketHandler.sendToAll(new ClientSyncPKT());

            sendSuccess(sender, "Reset EMC value for: " + name);
        }
        else
        {
            sendError(sender, "The EMC for " + name + "," + meta + " has not been modified!");
        }
	}
}
