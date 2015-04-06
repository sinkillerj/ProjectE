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
		return StatCollector.translateToLocal("pe.command.set.usage");
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
			sendError(sender, StatCollector.translateToLocal("pe.command.set.invalidparams"));
			return;
		}

		String name;
		int meta;
		int emc;

		if (params.length == 1)
		{
			ItemStack heldItem = getCommandSenderAsPlayer(sender).getHeldItem();

			if (heldItem == null)
			{
				sendError(sender, StatCollector.translateToLocal("pe.command.set.notholding"));
				return;
			}

			name = Item.itemRegistry.getNameForObject(heldItem.getItem());
			meta = heldItem.getItemDamage();
			emc = MathUtils.parseInteger(params[0]);

			if (emc < 0)
			{
				sendError(sender, String.format(StatCollector.translateToLocal("pe.command.set.invalidemc"), params[0]));
			}
		}
		else
		{
			name = params[0];
			meta = 0;
			boolean isOD = !name.contains(":");

			if (!isOD)
			{
				if (params.length > 2)
				{
					meta = MathUtils.parseInteger(params[1]);

					if (meta < 0)
					{
						sendError(sender, String.format(StatCollector.translateToLocal("pe.command.set.invalidmeta"), params[1]));
						return;
					}

					emc = MathUtils.parseInteger(params[2]);

					if (emc < 0)
					{
						sendError(sender, String.format(StatCollector.translateToLocal("pe.command.set.invalidemc"), params[0]));
						return;
					}
				}
				else
				{
					emc = MathUtils.parseInteger(params[1]);

					if (emc < 0)
					{
						sendError(sender, String.format(StatCollector.translateToLocal("pe.command.set.invalidemc"), params[0]));
						return;
					}
				}
			}
			else
			{
				emc = MathUtils.parseInteger(params[1]);

				if (emc < 0)
				{
					sendError(sender, String.format(StatCollector.translateToLocal("pe.command.set.invalidemc"), params[0]));
					return;
				}
			}
		}

		if (CustomEMCParser.addToFile(name, meta, emc))
		{
			EMCMapper.clearMaps();
			CustomEMCParser.readUserData();
			EMCMapper.map();
			TileEntityHandler.checkAllCondensers(sender.getEntityWorld());

			PacketHandler.sendFragmentedEmcPacketToAll();

			sendSuccess(sender, String.format(StatCollector.translateToLocal("pe.command.set.success"), name, emc));
		}
		else
		{
			sendError(sender, String.format(StatCollector.translateToLocal("pe.command.set.invaliditem"), name));
		}
	}
}
