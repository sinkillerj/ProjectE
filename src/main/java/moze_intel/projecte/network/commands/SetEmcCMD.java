package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.handlers.TileEntityHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
		if (params.length < 1)
		{
			sendError(sender, "Error: command needs parameters!");
			return;
		}

		String name;
		int[] metaRange = new int[2];
		int emc;

		if (params.length == 1)
		{
			ItemStack heldItem = getCommandSenderAsPlayer(sender).getHeldItem();

			if (heldItem == null)
			{
				sendError(sender, "Error: player isn't holding any item!");
				return;
			}

			name = Item.itemRegistry.getNameForObject(heldItem.getItem());
			metaRange[0] = metaRange[1] = heldItem.getItemDamage();
			emc = parseInteger(params[0]);

			if (emc < 0)
			{
				sendError(sender, "Error: " + params[0] + " isn't a valid number!");
			}
		}
		else
		{
			name = params[0];
			metaRange[0] = metaRange[1] = 0;
			boolean isOD = !name.contains(":");

			if (!isOD)
			{
				if (params.length > 2)
				{
					parseRange(params[1],metaRange);

					if (metaRange[0] < 0 || metaRange[1] < 0)
					{
						sendError(sender, "Error: " + params[1] + " isn't a valid range!");
						return;
					}

					emc = parseInteger(params[2]);

					if (emc < 0)
					{
						sendError(sender, "Error: " + params[1] + " isn't a valid number!");
						return;
					}
				}
				else
				{
					emc = parseInteger(params[1]);

					if (emc < 0)
					{
						sendError(sender, "Error: " + params[1] + " isn't a valid number!");
						return;
					}
				}
			}
			else
			{
				emc = parseInteger(params[1]);

				if (emc < 0)
				{
					sendError(sender, "Error: " + params[1] + " isn't a valid number!");
					return;
				}
			}
		}

		for (int meta = metaRange[0]; meta <= metaRange[1]; meta++) {
			if (!CustomEMCParser.addToFile(name, meta, emc)) {
				sendError(sender, "Error: couldn't find any valid items for: " + name + " with meta=" + meta);
				break;
			} else {
				sendSuccess(sender, "Registered EMC value for: " + name + "~" + meta+ "(" + emc + ")");
			}
		}
		EMCMapper.clearMaps();
		CustomEMCParser.readUserData();
		EMCMapper.map();
		TileEntityHandler.checkAllCondensers(sender.getEntityWorld());

		PacketHandler.sendFragmentedEmcPacketToAll();
	}
}
