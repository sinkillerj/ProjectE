package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.network.commands.argument.NSSItemArgument;
import moze_intel.projecte.network.commands.parser.NSSItemParser.NSSItemResult;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ResetEmcCMD {

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal("resetemc")
				.requires(cs -> cs.hasPermissionLevel(2))
				.then(Commands.argument("item", new NSSItemArgument())
						.executes(ctx -> resetEmc(ctx, NSSItemArgument.getNSS(ctx, "item"))))
				.executes(ctx -> resetEmc(ctx, RemoveEmcCMD.getHeldStack(ctx)));
	}

	private static int resetEmc(CommandContext<CommandSource> ctx, NSSItemResult stack) {
		String toReset = stack.getStringRepresentation();
		if (CustomEMCParser.removeFromFile(toReset)) {
			ctx.getSource().sendFeedback(PELang.COMMAND_RESET_SUCCESS.translate(toReset), true);
			ctx.getSource().sendFeedback(PELang.RELOAD_NOTICE.translate(), true);
			return Command.SINGLE_SUCCESS;
		}
		throw new CommandException(PELang.COMMAND_INVALID_ITEM.translate(toReset));
	}
}