package moze_intel.projecte.network.commands;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ClientKnowledgeClearPKT;
import moze_intel.projecte.playerData.Transmutation;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ClearKnowledgeCMD extends ProjectEBaseCMD {
	@Override
	public String getCommandName() {
		return "projecte_clearKnowledge";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/projecte_clearKnowledge <username> (optional)";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) {
		if (params.length == 0) {
			if (sender instanceof EntityPlayerMP) {
				Transmutation.clearKnowledge(sender.getCommandSenderName());
				PacketHandler.sendTo(new ClientKnowledgeClearPKT(sender.getCommandSenderName()), (EntityPlayerMP) sender);
				sendSuccess(sender, "Cleared transmutation knowledge for: " + sender.getCommandSenderName());
			} else {
				sendError(sender, "Can't clear knowledge for " + sender.getCommandSenderName());
			}
		} else {
			for (Object obj : sender.getEntityWorld().playerEntities) {
				EntityPlayer player = (EntityPlayer) obj;

				if (player.getCommandSenderName().equalsIgnoreCase(params[0])) {
					Transmutation.clearKnowledge(player.getCommandSenderName());
					PacketHandler.sendTo(new ClientKnowledgeClearPKT(player.getCommandSenderName()), (EntityPlayerMP) player);
					sendSuccess(sender, "Cleared transmutation knowledge for: " + player.getCommandSenderName());

					if (!player.getCommandSenderName().equals(sender.getCommandSenderName())) {
						player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Your transmutation knowledge was cleared! (by: " + sender.getCommandSenderName() + ")"));
					}

					return;
				}
			}

			sendError(sender, "Couldn't find player named: " + params[0]);
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}
}
