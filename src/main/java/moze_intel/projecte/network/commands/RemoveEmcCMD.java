package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.network.commands.argument.NSSItemArgument;
import moze_intel.projecte.network.commands.parser.NSSItemParser.NSSItemResult;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class RemoveEmcCMD {

	private static final SimpleCommandExceptionType EMPTY_STACK = new SimpleCommandExceptionType(PELang.COMMAND_REMOVE_NO_ITEM.translate());

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal("removeemc")
				.requires(cs -> cs.hasPermissionLevel(4))
				.then(Commands.argument("item", new NSSItemArgument())
						.executes(ctx -> removeEmc(ctx, NSSItemArgument.getNSS(ctx, "item"))))
				.executes(ctx -> removeEmc(ctx, getHeldStack(ctx)));
	}

	private static int removeEmc(CommandContext<CommandSource> ctx, NSSItemResult stack) {
		String toRemove = stack.getStringRepresentation();
		CustomEMCParser.addToFile(toRemove, 0);
		ctx.getSource().sendFeedback(PELang.COMMAND_REMOVE_SUCCESS.translate(toRemove), true);
		ctx.getSource().sendFeedback(PELang.RELOAD_NOTICE.translate(), true);
		return Command.SINGLE_SUCCESS;
	}

	public static NSSItemResult getHeldStack(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		ServerPlayerEntity player = ctx.getSource().asPlayer();
		ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);

		if (stack.isEmpty()) {
			stack = player.getHeldItem(Hand.OFF_HAND);
		}

		if (stack.isEmpty()) {
			throw EMPTY_STACK.create();
		}
		return new NSSItemResult(stack);
	}
}