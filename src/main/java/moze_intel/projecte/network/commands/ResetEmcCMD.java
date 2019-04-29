package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.config.CustomEMCParser;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

public class ResetEmcCMD
{
	public static LiteralArgumentBuilder<CommandSource> register()
	{
		return Commands.literal("resetemc")
				.requires(cs -> cs.hasPermissionLevel(4))
				.then(Commands.argument("item", ItemArgument.item())
						.executes(ctx -> resetEmc(ctx, new ItemStack(ItemArgument.getItem(ctx, "item").getItem()))))
				// todo 1.13 tag arg support?
				.executes(ctx -> {
					EntityPlayerMP player = ctx.getSource().asPlayer();
					ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

					if (stack.isEmpty())
					{
						stack = player.getHeldItem(EnumHand.OFF_HAND);
					}

					if (stack.isEmpty())
					{
						throw RemoveEmcCMD.EMPTY_STACK.create();
					}

					return resetEmc(ctx, stack);
				});
	}

	private static int resetEmc(CommandContext<CommandSource> ctx, ItemStack item)
	{
		if (CustomEMCParser.removeFromFile(item))
		{
			ctx.getSource().sendFeedback(new TextComponentTranslation("pe.command.reset.success", item.getItem().getRegistryName().toString()), true);
			ctx.getSource().sendFeedback(new TextComponentTranslation("pe.command.reload.notice"), true);
			return Command.SINGLE_SUCCESS;
		}
		else
		{
			throw new CommandException(new TextComponentTranslation("pe.command.remove.invaliditem", item.getItem().getRegistryName()));
		}
	}
}
