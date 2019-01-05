package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import moze_intel.projecte.config.CustomEMCParser;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class RemoveEmcCMD
{
	// todo 1.13 localize
	public static final SimpleCommandExceptionType EMPTY_STACK = new SimpleCommandExceptionType(new TextComponentString("Empty stack in both hands"));

	public static LiteralArgumentBuilder<CommandSource> register()
	{
		return Commands.literal("removeEMC")
				.requires(cs -> cs.hasPermissionLevel(4))
				.then(Commands.argument("item", ItemArgument.item())
					// todo 1.13 dropping nbt info, use a more restrictive arg parser?
					.executes(ctx -> removeEmc(ctx, ItemArgument.getItem(ctx, "item").getItem())))
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
						throw EMPTY_STACK.create();
					}

					return removeEmc(ctx, stack.getItem());
				});
	}

	private static int removeEmc(CommandContext<CommandSource> ctx, Item item)
	{
		if (CustomEMCParser.addToFile(item.getRegistryName().toString(), 0))
		{
			ctx.getSource().sendFeedback(new TextComponentTranslation("pe.command.remove.success", item.getRegistryName().toString()), true);
			ctx.getSource().sendFeedback(new TextComponentTranslation("pe.command.reload.notice"), true);
			return Command.SINGLE_SUCCESS;
		}
		else
		{
			// todo 1.13 will this else ever happen?
			// throw new CommandException("pe.command.remove.invaliditem", name);
			return 0;
		}
	}
}
