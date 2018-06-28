package moze_intel.projecte.network.commands;

import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.command.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

public class SetEmcCMD extends CommandBase
{
	@Nonnull
	@Override
	public String getName()
	{
		return "setEMC";
	}

	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender)
	{
		return "pe.command.set.usage";
	}
	
	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException
	{
		if (params.length < 1)
		{
			throw new WrongUsageException(getUsage(sender));
		}

		String name;
		int meta;
		long emc;

		if (params.length == 1)
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

			name = heldItem.getItem().getRegistryName().toString();
			meta = heldItem.getItemDamage();
			emc = Long.parseLong(params[0]);

			if (emc < 0)
			{
				throw new NumberInvalidException("pe.command.set.invalidemc", params[0]);
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
						throw new CommandException("pe.command.set.invalidmeta", params[1]);
					}

					emc = MathUtils.parseInteger(params[2]);

					if (emc < 0)
					{
						throw new CommandException("pe.command.set.invalidemc", params[2]);
					}
				}
				else
				{
					emc = MathUtils.parseInteger(params[1]);

					if (emc < 0)
					{
						throw new NumberInvalidException("pe.command.set.invalidemc", params[1]);
					}
				}
			}
			else
			{
				emc = MathUtils.parseInteger(params[1]);

				if (emc < 0)
				{
					throw new NumberInvalidException("pe.command.set.invalidemc", params[1]);
				}
			}
		}

		if (CustomEMCParser.addToFile(name, meta, emc))
		{
			sender.sendMessage(new TextComponentTranslation("pe.command.set.success", name, emc));
			sender.sendMessage(new TextComponentTranslation("pe.command.reload.notice"));
		}
		else
		{
			throw new CommandException("pe.command.set.invaliditem", name);
		}
	}
}
