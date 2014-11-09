package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.EMCMapper;
import net.minecraft.command.ICommandSender;

public class SetEmcCMD extends ProjectEBaseCMD
{
	@Override
	public String getCommandName() 
	{
		return "projecte_setEMC";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) 
	{
		return "/projecte_setEMC <unlocalized-name/ore dictionary name> <metadata (optional)> <EMC value>";
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

        String name = params[0];
        int meta = 0;
        boolean isOD = !name.contains(":");

        if (!isOD && params.length > 2)
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

        int emc = 0;

        if (isOD)
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

            if (params.length > 2)
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

        if (emc <= 0)
        {
            sendError(sender, "Error: the EMC value needs to be greater than 0!");
            return;
        }

        if (CustomEMCParser.addToFile(name, meta, emc))
        {
            EMCMapper.clearMaps();
            CustomEMCParser.readUserData();
            EMCMapper.map();
            //PacketHandler.sendToAll(new ClientSyncPKT());

            sendSuccess(sender, "Registered EMC value for: " + name + "(" + emc + ")");
        }
        else
        {
            sendError(sender, "Error: couldn't find any valid items for: " + name);
        }
	}
}
