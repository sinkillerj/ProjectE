package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

public class ResetEmcCMD extends CommandBase
{
	@Nonnull
	@Override
	public String getName()
	{
		return "resetEMC";
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender)
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
		String name;
		int meta = 0;

		if (params.length == 0)
		{
			ItemStack heldItem = getCommandSenderAsPlayer(sender).getHeldItem(EnumHand.MAIN_HAND);

			if (heldItem.isEmpty())
			{
				heldItem = getCommandSenderAsPlayer(sender).getHeldItem(EnumHand.OFF_HAND);
			}

			if (heldItem.isEmpty())
			{
				throw new WrongUsageException(getUsage(sender));
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
					throw new CommandException("pe.command.reset.invalidmeta", params[1]);
				}
			}
		}

		if (CustomEMCParser.removeFromFile(name, meta))
		{
			sender.sendMessage(new TextComponentTranslation("pe.command.reset.success", name));
			sender.sendMessage(new TextComponentTranslation("pe.command.reload.notice"));
		}
		else
		{
			throw new CommandException("pe.command.reset.nochange", name, meta);
		}
	}
}
