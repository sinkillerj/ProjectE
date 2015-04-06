package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.handlers.TileEntityHandler;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

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
		return StatCollector.translateToLocal("pe.command.remove.usage");
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		String name;
		int meta = 0;

		if (params.length == 0)
		{
			ItemStack heldItem = getCommandSenderAsPlayer(sender).getHeldItem();

			if (heldItem == null)
			{
				sendError(sender, StatCollector.translateToLocal("pe.command.remove.notholding"));
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
				meta = MathUtils.parseInteger(params[1]);

				if (meta < 0)
				{
					sendError(sender, String.format(StatCollector.translateToLocal("pe.command.remove.invalidmeta"), params[1]));
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

			sendSuccess(sender, String.format(StatCollector.translateToLocal("pe.command.remove.success"), name));
		}
		else
		{
			sendError(sender, String.format(StatCollector.translateToLocal("pe.command.remove.invaliditem"), name));
		}
	}
}
