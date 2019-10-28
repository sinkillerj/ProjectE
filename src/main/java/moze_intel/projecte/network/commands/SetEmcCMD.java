package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.config.CustomEMCParser;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;

public class SetEmcCMD {

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal("setemc")
				.requires(cs -> cs.hasPermissionLevel(4))
				.then(Commands.argument("emc", LongArgumentType.longArg(0, Long.MAX_VALUE))
						.then(Commands.argument("item", ItemArgument.item())
								// todo 1.13 dropping nbt info, use a more restrictive arg parser?
								// todo 1.13 tag arg support?
								.executes(ctx -> setEmc(ctx, LongArgumentType.getLong(ctx, "emc"), ItemArgument.getItem(ctx, "item").getItem())))
						.executes(ctx -> {
							ServerPlayerEntity player = ctx.getSource().asPlayer();
							ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);

							if (stack.isEmpty()) {
								stack = player.getHeldItem(Hand.OFF_HAND);
							}

							if (stack.isEmpty()) {
								throw RemoveEmcCMD.EMPTY_STACK.create();
							}

							return setEmc(ctx, LongArgumentType.getLong(ctx, "emc"), stack.getItem());
						}));

	}

	private static int setEmc(CommandContext<CommandSource> ctx, long emc, Item item) {
		CustomEMCParser.addToFile(item.getRegistryName().toString(), emc);
		ctx.getSource().sendFeedback(new TranslationTextComponent("pe.command.set.success", item.getRegistryName().toString(), emc), true);
		ctx.getSource().sendFeedback(new TranslationTextComponent("pe.command.reload.notice"), true);
		return Command.SINGLE_SUCCESS;
	}
}