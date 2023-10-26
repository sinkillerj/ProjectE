package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.math.BigInteger;
import java.util.Optional;
import moze_intel.projecte.PEPermissions;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import moze_intel.projecte.utils.text.PELang;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class EMCCMD {

	private enum ActionType {
		ADD,
		REMOVE,
		SET,
		GET,
		TEST
	}

	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
		return Commands.literal("emc")
				.requires(PEPermissions.COMMAND_EMC)
				.then(Commands.literal("add")
						.requires(PEPermissions.COMMAND_EMC_ADD)
						.then(executeWithParameters(ActionType.ADD))
				)
				.then(Commands.literal("remove")
						.requires(PEPermissions.COMMAND_EMC_REMOVE)
						.then(executeWithParameters(ActionType.REMOVE))
				)
				.then(Commands.literal("set")
						.requires(PEPermissions.COMMAND_EMC_SET)
						.then(executeWithParameters(ActionType.SET))
				)
				.then(Commands.literal("test")
						.requires(PEPermissions.COMMAND_EMC_TEST)
						.then(executeWithParameters(ActionType.TEST))
				)
				.then(Commands.literal("get")
						.requires(PEPermissions.COMMAND_EMC_GET)
						.then(Commands.argument("player", EntityArgument.player())
								.executes((ctx) -> handle(ctx, ActionType.GET))
						)
				);
	}

	private static ArgumentBuilder<CommandSourceStack, ?> executeWithParameters(ActionType actionType) {
		return Commands.argument("player", EntityArgument.player())
				.then(Commands.argument("value", StringArgumentType.string())
						.executes(ctx -> handle(ctx, actionType))
				);
	}

	private static MutableComponent formatEMC(BigInteger emc) {
		return TextComponentUtil.build(ChatFormatting.GRAY, TransmutationEMCFormatter.formatEMC(emc));
	}

	private static int handle(CommandContext<CommandSourceStack> ctx, ActionType action) throws CommandSyntaxException {
		CommandSourceStack source = ctx.getSource();
		ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
		Optional<IKnowledgeProvider> cap = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).resolve();
		if (cap.isEmpty()) {
			source.sendFailure(PELang.COMMAND_PROVIDER_FAIL.translate(player.getDisplayName()));
			return 0;
		}
		IKnowledgeProvider provider = cap.get();
		if (action == ActionType.GET) {
			source.sendSuccess(PELang.COMMAND_EMC_GET_SUCCESS.translate(player.getDisplayName(), formatEMC(provider.getEmc())), true);
			return Command.SINGLE_SUCCESS;
		}
		String val = StringArgumentType.getString(ctx, "value");
		@Nullable BigInteger value = null;

		try {
			value = new BigInteger(val);
			if (value.compareTo(BigInteger.ZERO) < 0) {
				switch (action) {
					case ADD, REMOVE -> {
						action = action == ActionType.ADD ? ActionType.REMOVE : ActionType.ADD;
						value = value.abs();
					}
					case SET, TEST -> value = null;
				}
			}
		} catch (NumberFormatException ignore) {
		}
		if (value == null) {
			source.sendFailure(PELang.COMMAND_EMC_INVALID.translate(val));
			return 0;
		}

		BigInteger newEMC = provider.getEmc();
		switch (action) {
			case ADD -> {
				newEMC = newEMC.add(value);
				source.sendSuccess(PELang.COMMAND_EMC_ADD_SUCCESS.translate(formatEMC(value), player.getDisplayName(), formatEMC(newEMC)), true);
			}
			case REMOVE -> {
				newEMC = newEMC.subtract(value);
				if (newEMC.compareTo(BigInteger.ZERO) < 0) {
					source.sendFailure(PELang.COMMAND_EMC_NEGATIVE.translate(formatEMC(value), player.getDisplayName()));
					return 0;
				}
				source.sendSuccess(PELang.COMMAND_EMC_REMOVE_SUCCESS.translate(formatEMC(value), player.getDisplayName(), formatEMC(newEMC)), true);
			}
			case SET -> {
				newEMC = value;
				source.sendSuccess(PELang.COMMAND_EMC_SET_SUCCESS.translate(player.getDisplayName(), formatEMC(value)), true);
			}
			case TEST -> {
				if (newEMC.compareTo(value) >= 0) {
					source.sendSuccess(PELang.COMMAND_EMC_TEST_SUCCESS.translateColored(ChatFormatting.GREEN, player.getDisplayName(), formatEMC(value)), true);
					return Command.SINGLE_SUCCESS;
				}

				source.sendFailure(PELang.COMMAND_EMC_TEST_FAIL.translate(player.getDisplayName(), formatEMC(value)));
				return 0;
			}
		}

		provider.setEmc(newEMC);
		provider.syncEmc(player);
		return Command.SINGLE_SUCCESS;
	}
}
