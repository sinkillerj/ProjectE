package moze_intel.projecte.network.commands;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeClearPKT;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;

public class ClearKnowledgeCMD extends ProjectEBaseCMD
{
	@Nonnull
	@Override
	public String getCommandName() 
	{
		return "projecte_clearKnowledge";
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(@Nonnull ICommandSender sender)
	{
		return "pe.command.clearknowledge.usage";
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params)
	{
		if (params.length == 0)
		{
			if (sender instanceof EntityPlayerMP)
			{
				((EntityPlayerMP) sender).getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).clearKnowledge();
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
			for (EntityPlayer player : sender.getEntityWorld().playerEntities)
			{
				if (player.getName().equalsIgnoreCase(params[0]))
				{
					((EntityPlayerMP) sender).getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).clearKnowledge();
					PacketHandler.sendTo(new KnowledgeClearPKT(), (EntityPlayerMP) player);
					sendSuccess(sender, new TextComponentTranslation("pe.command.clearknowledge.success", player.getName()));
					
					if (!player.getName().equals(sender.getName()))
					{
						player.addChatComponentMessage(new TextComponentTranslation("pe.command.clearknowledge.notify", sender.getName()).setStyle(new Style().setColor(TextFormatting.RED)));
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
