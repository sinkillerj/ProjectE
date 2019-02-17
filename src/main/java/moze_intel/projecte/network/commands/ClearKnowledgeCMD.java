package moze_intel.projecte.network.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeClearPKT;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;

public class ClearKnowledgeCMD
{
	public static ArgumentBuilder<CommandSource, ?> register()
	{
		return Commands.literal("clearknowledge")
				.requires(cs -> cs.hasPermissionLevel(4))
				.then(Commands.argument("targets", EntityArgument.players())
						.executes(cs -> execute(cs, EntityArgument.getPlayers(cs, "targets"))));
	}

	private static int execute(CommandContext<CommandSource> ctx, Collection<EntityPlayerMP> targets)
	{
		for (EntityPlayerMP player : targets)
		{
			player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY).ifPresent(IKnowledgeProvider::clearKnowledge);
			PacketHandler.sendTo(new KnowledgeClearPKT(), player);
			ctx.getSource().sendFeedback(new TextComponentTranslation("pe.command.clearknowledge.success", player.getDisplayName()), true);

			if (player != ctx.getSource().getEntity())
			{
				player.sendMessage(new TextComponentTranslation("pe.command.clearknowledge.notify", ctx.getSource().getDisplayName()).setStyle(new Style().setColor(TextFormatting.RED)));
			}
		}

		return targets.size();
	}
}
