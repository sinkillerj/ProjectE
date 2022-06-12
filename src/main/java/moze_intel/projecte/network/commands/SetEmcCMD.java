package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.network.commands.argument.NSSItemArgument;
import moze_intel.projecte.network.commands.parser.NSSItemParser.NSSItemResult;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SetEmcCMD {

	public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext context) {
		return Commands.literal("setemc")
				.requires(cs -> cs.hasPermission(2))
				.then(Commands.argument("emc", LongArgumentType.longArg(0, Long.MAX_VALUE))
						.then(Commands.argument("item", NSSItemArgument.nss(context))
								.executes(ctx -> setEmc(ctx, NSSItemArgument.getNSS(ctx, "item"), LongArgumentType.getLong(ctx, "emc"))))
						.executes(ctx -> setEmc(ctx, RemoveEmcCMD.getHeldStack(ctx), LongArgumentType.getLong(ctx, "emc"))));

	}

	private static int setEmc(CommandContext<CommandSourceStack> ctx, NSSItemResult stack, long emc) {
		String toSet = stack.getStringRepresentation();
		CustomEMCParser.addToFile(toSet, emc);
		ctx.getSource().sendSuccess(PELang.COMMAND_SET_SUCCESS.translate(toSet, emc), true);
		ctx.getSource().sendSuccess(PELang.RELOAD_NOTICE.translate(), true);
		return Command.SINGLE_SUCCESS;
	}
}