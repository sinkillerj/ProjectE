package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.network.PacketHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextComponentTranslation;

public class ReloadEmcCMD
{
	public static LiteralArgumentBuilder<CommandSource> register()
	{
		return Commands.literal("reloadEMC")
				.requires(cs -> cs.hasPermissionLevel(4))
				.executes(cs -> {
					cs.getSource().sendFeedback(new TextComponentTranslation("pe.command.reload.started"), true);

					EMCMapper.clearMaps();
					CustomEMCParser.init();
					EMCMapper.map();

					cs.getSource().sendFeedback(new TextComponentTranslation("pe.command.reload.success"), true);

					PacketHandler.sendFragmentedEmcPacketToAll();
					return Command.SINGLE_SUCCESS;
				});
	}
}
