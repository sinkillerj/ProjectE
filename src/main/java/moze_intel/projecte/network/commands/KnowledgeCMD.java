package moze_intel.projecte.network.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import moze_intel.projecte.PEPermissions;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.emc.nbt.NBTManager;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class KnowledgeCMD {
	private enum ActionType {
		LEARN,
		UNLEARN,
		CLEAR,
		TEST
	}
	public static ArgumentBuilder<CommandSourceStack, ?> register(CommandBuildContext context) {
		return Commands.literal("knowledge")
			.then(Commands.literal("clear")
				.requires(PEPermissions.COMMAND_KNOWLEDGE_CLEAR)
				.then(Commands.argument("player", EntityArgument.player())
					.executes((ctx) -> handle(ctx, ActionType.CLEAR))
				)
			)
			.then(Commands.literal("learn")
				.requires(PEPermissions.COMMAND_KNOWLEDGE_LEARN)
				.then(executeWithParameters(ActionType.LEARN, context))
			)
			.then(Commands.literal("unlearn")
				.requires(PEPermissions.COMMAND_KNOWLEDGE_UNLEARN)
				.then(executeWithParameters(ActionType.UNLEARN, context))
			)
			.then(Commands.literal("test")
				.requires(PEPermissions.COMMAND_KNOWLEDGE_TEST)
				.then(executeWithParameters(ActionType.TEST, context))
			);
	}

	private static ArgumentBuilder<CommandSourceStack, ?> executeWithParameters(ActionType actionType, CommandBuildContext context) {
		return Commands.argument("player", EntityArgument.player())
			.then(Commands.argument("item", ItemArgument.item(context))
				.executes(ctx -> handle(ctx, actionType))
			);
	}

	private static int handle(CommandContext<CommandSourceStack> ctx, ActionType action) throws CommandSyntaxException {
		ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
		Optional<IKnowledgeProvider> cap = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).resolve();
		if (cap.isEmpty()) {
			ctx.getSource().sendFailure(PELang.COMMAND_PROVIDER_FAIL.translateColored(ChatFormatting.RED));
			return 0;
		}
		IKnowledgeProvider provider = cap.get();
		if (action == ActionType.CLEAR) {
			if (provider.getKnowledge().size() == 0) {
				ctx.getSource().sendFailure(PELang.COMMAND_KNOWLEDGE_CLEAR_FAIL.translateColored(ChatFormatting.RED, player.getDisplayName()));
				return 0;
			}
			provider.clearKnowledge();
			provider.sync(player);
			ctx.getSource().sendSuccess(PELang.COMMAND_KNOWLEDGE_CLEAR_SUCCESS.translateColored(ChatFormatting.GREEN, player.getDisplayName()), true);
			return 1;
		}

		ItemStack item = new ItemStack(ItemArgument.getItem(ctx, "item").getItem());

		if (!EMCHelper.doesItemHaveEmc(item)) {
			ctx.getSource().sendFailure(PELang.COMMAND_KNOWLEDGE_INVALID.translateColored(ChatFormatting.RED, item.getDisplayName()));
			return 0;
		}

		switch (action) {
			case LEARN -> {
				if (provider.hasKnowledge(item)) {
					ctx.getSource().sendFailure(PELang.COMMAND_KNOWLEDGE_LEARN_FAIL.translateColored(ChatFormatting.RED, player.getDisplayName(), item.getDisplayName()));
					return 0;
				} else {
					provider.addKnowledge(item);
					provider.sync(player);
					ctx.getSource().sendSuccess(PELang.COMMAND_KNOWLEDGE_LEARN_SUCCESS.translateColored(ChatFormatting.GREEN, player.getDisplayName(), item.getDisplayName()), true);
				}
			}
			case UNLEARN -> {
				if (!provider.hasKnowledge(item)) {
					ctx.getSource().sendFailure(PELang.COMMAND_KNOWLEDGE_UNLEARN_FAIL.translateColored(ChatFormatting.RED, player.getDisplayName(), item.getDisplayName()));
					return 0;
				} else {
					provider.removeKnowledge(item);
					provider.sync(player);
					ctx.getSource().sendSuccess(PELang.COMMAND_KNOWLEDGE_UNLEARN_SUCCESS.translateColored(ChatFormatting.GREEN, player.getDisplayName(), item.getDisplayName()), true);
				}
			}
			case TEST -> {
				if (provider.hasKnowledge(item)) {
					ctx.getSource().sendSuccess(PELang.COMMAND_KNOWLEDGE_TEST_SUCCESS.translateColored(ChatFormatting.GREEN, player.getDisplayName(), item.getDisplayName()), true);
					return 1;
				} else {
					ctx.getSource().sendFailure(PELang.COMMAND_KNOWLEDGE_TEST_FAIL.translateColored(ChatFormatting.RED, player.getDisplayName(), item.getDisplayName()));
					return 0;
				}
			}
		}
		provider.syncKnowledgeChange(player, NBTManager.getPersistentInfo(ItemInfo.fromStack(item)), true);

		return 1;
	}
}
