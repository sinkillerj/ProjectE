package moze_intel.projecte.network.commands;

import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeClearPKT;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.ChatHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

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
	public void execute(MinecraftServer server, ICommandSender sender, String[] params)
	{
		if (params.length == 0)
		{
			if (sender instanceof EntityPlayerMP)
			{
				Transmutation.clearKnowledge(((EntityPlayerMP) sender));
				PacketHandler.sendTo(new KnowledgeClearPKT(), (EntityPlayerMP) sender);
				sendSuccess(sender, new TextComponentTranslation("pe.command.clearknowledge.success", sender.getName()));
			}
			else
			{
				sendError(sender, new TextComponentTranslation("pe.command.clearknowledge.error", sender.getName()));
			}
		}
		else
		{
			for (Object obj : sender.getEntityWorld().playerEntities)
			{
				EntityPlayer player = (EntityPlayer) obj;
				
				if (player.getName().equalsIgnoreCase(params[0]))
				{
					Transmutation.clearKnowledge(player);
					PacketHandler.sendTo(new KnowledgeClearPKT(), (EntityPlayerMP) player);
					sendSuccess(sender, new TextComponentTranslation("pe.command.clearknowledge.success", player.getName()));
					
					if (!player.getName().equals(sender.getName()))
					{
						player.addChatComponentMessage(ChatHelper.modifyColor(new TextComponentTranslation("pe.command.clearknowledge.notify", sender.getName()), TextFormatting.RED));
					}
					
					return;
				}
			}

			sendError(sender, new TextComponentTranslation("pe.command.clearknowledge.playernotfound", params[0]));
		}
	}

	@Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}
}
