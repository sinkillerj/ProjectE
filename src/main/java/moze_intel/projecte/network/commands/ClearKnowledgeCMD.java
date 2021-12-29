package moze_intel.projecte.network.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.to_client.knowledge.KnowledgeClearPKT;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class ClearKnowledgeCMD {

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("clearknowledge")
				.requires(cs -> cs.hasPermission(2))
				.then(Commands.argument("targets", EntityArgument.players())
						.executes(cs -> execute(cs, EntityArgument.getPlayers(cs, "targets"))));
	}

	private static int execute(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> targets) {
		CommandSourceStack source = ctx.getSource();
		for (ServerPlayer player : targets) {
			player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(IKnowledgeProvider::clearKnowledge);
			PacketHandler.sendTo(new KnowledgeClearPKT(), player);
			source.sendSuccess(PELang.CLEAR_KNOWLEDGE_SUCCESS.translate(player.getDisplayName()), true);
			if (player != source.getEntity()) {
				player.sendMessage(PELang.CLEAR_KNOWLEDGE_NOTIFY.translateColored(ChatFormatting.RED, source.getDisplayName()), Util.NIL_UUID);
			}
		}
		return targets.size();
	}
}