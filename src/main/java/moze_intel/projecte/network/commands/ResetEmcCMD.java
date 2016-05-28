package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

public class ResetEmcCMD extends ProjectEBaseCMD
{
	@Nonnull
	@Override
	public String getCommandName() 
	{
		return "projecte_resetEMC";
	}

	@Nonnull
	@Override
	public String getCommandUsage(@Nonnull ICommandSender sender)
	{
		return "pe.command.reset.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException
	{
		String name = "";
		int meta = 0;

		if (params.length == 0)
		{
			ItemStack heldItem = getCommandSenderAsPlayer(sender).getHeldItem(EnumHand.MAIN_HAND);

			if (heldItem == null)
			{
				heldItem = getCommandSenderAsPlayer(sender).getHeldItem(EnumHand.OFF_HAND);
			}

			if (heldItem == null)
			{
				sendError(sender, new TextComponentTranslation("pe.command.reset.usage"));
				return;
			}

			name = Item.REGISTRY.getNameForObject(heldItem.getItem()).toString();
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
					sendError(sender, new TextComponentTranslation("pe.command.reset.invalidmeta", params[1]));
					return;
				}
			}
		}

		if (CustomEMCParser.removeFromFile(name, meta))
		{
			sender.addChatMessage(new TextComponentTranslation("pe.command.reset.success", name));
			sender.addChatMessage(new TextComponentTranslation("pe.command.reload.notice"));
		}
		else
		{
			sendError(sender, new TextComponentTranslation("pe.command.reset.nochange", name, meta));
		}
	}
}
