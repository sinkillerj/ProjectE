package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.config.CustomEMCParser;
import net.minecraft.command.*;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;

public class SetEmcCMD
{
	public static LiteralArgumentBuilder<CommandSource> register()
	{
		return Commands.literal("setemc")
				.requires(cs -> cs.hasPermissionLevel(4))
				.then(Commands.argument("emc", IntegerArgumentType.integer(0))
					.then(Commands.argument("item", ItemArgument.item())
						// todo 1.13 dropping nbt info, use a more restrictive arg parser?
						// todo 1.13 tag arg support?
						// todo support longs
						.executes(ctx -> setEmc(ctx, IntegerArgumentType.getInteger(ctx, "emc"), new ItemStack(ItemArgument.getItem(ctx, "item").getItem()))))
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

						return setEmc(ctx, IntegerArgumentType.getInteger(ctx, "emc"), stack);
					}));

	}

	private static int setEmc(CommandContext<CommandSource> ctx, int emc, ItemStack item)
	{
		CustomEMCParser.addToFile(item, emc);
		ctx.getSource().sendFeedback(new TextComponentTranslation("pe.command.set.success", item.getItem().getRegistryName().toString(), emc), true);
		ctx.getSource().sendFeedback(new TextComponentTranslation("pe.command.reload.notice"), true);
		return Command.SINGLE_SUCCESS;
	}
}
