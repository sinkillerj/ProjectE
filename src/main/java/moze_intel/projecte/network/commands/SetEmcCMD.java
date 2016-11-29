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

public class SetEmcCMD extends ProjectEBaseCMD
{
	@Nonnull
	@Override
	public String getCommandName() 
	{
		return "projecte_setEMC";
	}

	@Nonnull
	@Override
	public String getCommandUsage(@Nonnull ICommandSender sender)
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
			sendError(sender, new TextComponentTranslation("pe.command.set.usage"));
			return;
		}

		String name;
		int meta;
		int emc;

		if (params.length == 1)
		{
			ItemStack heldItem = getCommandSenderAsPlayer(sender).getHeldItem(EnumHand.MAIN_HAND);
			if (heldItem == null)
			{
				heldItem = getCommandSenderAsPlayer(sender).getHeldItem(EnumHand.OFF_HAND);
			}

			if (heldItem == null)
			{
				sendError(sender, new TextComponentTranslation("pe.command.set.usage"));
				return;
			}

			name = heldItem.getItem().getRegistryName().toString();
			meta = heldItem.getItemDamage();
			emc = MathUtils.parseInteger(params[0]);

			if (emc < 0)
			{
				sendError(sender, new TextComponentTranslation("pe.command.set.invalidemc", params[0]));
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
						sendError(sender, new TextComponentTranslation("pe.command.set.invalidmeta", params[1]));
						return;
					}

					emc = MathUtils.parseInteger(params[2]);

					if (emc < 0)
					{
						sendError(sender, new TextComponentTranslation("pe.command.set.invalidemc", params[0]));
						return;
					}
				}
				else
				{
					emc = MathUtils.parseInteger(params[1]);

					if (emc < 0)
					{
						sendError(sender, new TextComponentTranslation("pe.command.set.invalidemc", params[0]));
						return;
					}
				}
			}
			else
			{
				emc = MathUtils.parseInteger(params[1]);

				if (emc < 0)
				{
					sendError(sender, new TextComponentTranslation("pe.command.set.invalidemc", params[0]));
					return;
				}
			}
		}

		if (CustomEMCParser.addToFile(name, meta, emc))
		{
			sender.addChatMessage(new TextComponentTranslation("pe.command.set.success", name, emc));
			sender.addChatMessage(new TextComponentTranslation("pe.command.reload.notice"));
		}
		else
		{
			sendError(sender, new TextComponentTranslation("pe.command.set.invaliditem", name));
		}
	}
}
