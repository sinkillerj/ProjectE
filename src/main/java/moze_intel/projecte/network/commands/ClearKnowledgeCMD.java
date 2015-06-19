package moze_intel.projecte.network.commands;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeClearPKT;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.ChatHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class ClearKnowledgeCMD extends ProjectEBaseCMD
{
	@Override
	public String getCommandName() 
	{
		return "projecte_clearKnowledge";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "pe.command.clearknowledge.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		if (params.length == 0)
		{
			if (sender instanceof EntityPlayerMP)
			{
				Transmutation.clearKnowledge(((EntityPlayerMP) sender));
				PacketHandler.sendTo(new KnowledgeClearPKT(), (EntityPlayerMP) sender);
				sendSuccess(sender, new ChatComponentTranslation("pe.command.clearknowledge.success", sender.getCommandSenderName()));
			}
			else
			{
				sendError(sender, new ChatComponentTranslation("pe.command.clearknowledge.error", sender.getCommandSenderName()));
			}
		}
		else
		{
			for (Object obj : sender.getEntityWorld().playerEntities)
			{
				EntityPlayer player = (EntityPlayer) obj;
				
				if (player.getCommandSenderName().equalsIgnoreCase(params[0]))
				{
					Transmutation.clearKnowledge(player);
					PacketHandler.sendTo(new KnowledgeClearPKT(), (EntityPlayerMP) player);
					sendSuccess(sender, new ChatComponentTranslation("pe.command.clearknowledge.success", player.getCommandSenderName()));
					
					if (!player.getCommandSenderName().equals(sender.getCommandSenderName()))
					{
						player.addChatComponentMessage(ChatHelper.modifyColor(new ChatComponentTranslation("pe.command.clearknowledge.notify", sender.getCommandSenderName()), EnumChatFormatting.RED));
					}
					
					return;
				}
			}

			sendError(sender, new ChatComponentTranslation("pe.command.clearknowledge.playernotfound", params[0]));
		}
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}
}
