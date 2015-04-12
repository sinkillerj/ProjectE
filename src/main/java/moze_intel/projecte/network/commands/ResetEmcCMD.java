package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.ThreadReloadEMCMap;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;

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
		return "pe.command.reset.usage";
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
				sendError(sender, new ChatComponentTranslation("pe.command.reset.notholding"));
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
					sendError(sender, new ChatComponentTranslation("pe.command.reset.invalidmeta", params[1]));
					return;
				}
			}
		}

		if (CustomEMCParser.removeFromFile(name, meta))
		{
			ThreadReloadEMCMap.runEMCRemap(false, sender.getEntityWorld());

			sendSuccess(sender, new ChatComponentTranslation("pe.command.reset.success", name));
		}
		else
		{
			sendError(sender, new ChatComponentTranslation("pe.command.reset.nochange", name, meta));
		}
	}
}
