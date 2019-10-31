package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.network.commands.argument.NSSItemArgument;
import moze_intel.projecte.network.commands.parser.NSSItemParser.NSSItemResult;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class ResetEmcCMD {

	public static LiteralArgumentBuilder<CommandSource> register() {
		return Commands.literal("resetemc")
				.requires(cs -> cs.hasPermissionLevel(4))
				.then(Commands.argument("item", NSSItemArgument.itemArgument())
						.executes(ctx -> resetEmc(ctx, NSSItemArgument.getNSS(ctx, "item"))))
				.executes(ctx -> resetEmc(ctx, RemoveEmcCMD.getHeldStack(ctx)));
	}

	private static int resetEmc(CommandContext<CommandSource> ctx, NSSItemResult stack) {
		String toReset = stack.getStringRepresentation();
		if (CustomEMCParser.removeFromFile(toReset)) {
			ctx.getSource().sendFeedback(new TranslationTextComponent("pe.command.reset.success", toReset), true);
			ctx.getSource().sendFeedback(new TranslationTextComponent("pe.command.reload.notice"), true);
			return Command.SINGLE_SUCCESS;
		}
		throw new CommandException(new TranslationTextComponent("pe.command.remove.invaliditem", toReset));
	}
}