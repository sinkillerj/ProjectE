package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.handlers.TileEntityHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
		return "/projecte_removeEMC <unlocalized/ore-dictionary name> <metadata (optional)>";
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		String name = "";
		int meta = 0;

		if (params.length == 0)
		{
			ItemStack heldItem = getCommandSenderAsPlayer(sender).getHeldItem();

			if (heldItem == null)
			{
				sendError(sender, "Error: player isn't holding any item!");
				return;
			}

			name = Item.itemRegistry.getNameForObject(heldItem.getItem());
			meta = heldItem.getItemDamage();
		}
		else
		{
			name = params[0];

			if (params.length > 1)
			{
				meta = parseInteger(params[1]);

				if (meta < 0)
				{
					sendError(sender, "Error: the metadata passed (" + params[1] + ") is not a valid number!");
					return;
				}
			}
		}

		if (CustomEMCParser.addToFile(name, meta, 0))
		{
			EMCMapper.clearMaps();
			CustomEMCParser.readUserData();
			EMCMapper.map();
			TileEntityHandler.checkAllCondensers(sender.getEntityWorld());

			PacketHandler.sendFragmentedEmcPacketToAll();

			sendSuccess(sender, "Removed EMC value for: " + name);
		}
		else
		{
			sendError(sender, "Error: couldn't find any valid items for: " + name);
		}
	}
}
