package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import moze_intel.projecte.PEPermissions;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.network.commands.argument.NSSItemArgument;
import moze_intel.projecte.network.commands.parser.NSSItemParser.NSSItemResult;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ResetEmcCMD {

	private static final DynamicCommandExceptionType INVALID_ITEM = new DynamicCommandExceptionType(PELang.COMMAND_INVALID_ITEM::translate);

	public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext context) {
		return Commands.literal("resetemc")
				.requires(PEPermissions.COMMAND_RESET_EMC)
				.then(Commands.argument("item", NSSItemArgument.nss(context))
						.executes(ctx -> resetEmc(ctx, NSSItemArgument.getNSS(ctx, "item"))))
				.executes(ctx -> resetEmc(ctx, RemoveEmcCMD.getHeldStack(ctx)));
	}

	private static int resetEmc(CommandContext<CommandSourceStack> ctx, NSSItemResult stack) throws CommandSyntaxException {
		String toReset = stack.getStringRepresentation();
		if (CustomEMCParser.removeFromFile(toReset)) {
			ctx.getSource().sendSuccess(() -> PELang.COMMAND_RESET_SUCCESS.translate(toReset), true);
			ctx.getSource().sendSuccess(PELang.RELOAD_NOTICE::translate, true);
			return Command.SINGLE_SUCCESS;
		}
		throw INVALID_ITEM.create(toReset);
	}
}