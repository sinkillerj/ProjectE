package moze_intel.projecte.network.commands;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.KnowledgeClearPKT;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClearKnowledgeCMD extends CommandBase
{

    private static final List<String> DEFAULT_SELECTORS = Arrays.asList("@p", "@a", "@r", "@e", "@s");

	@Nonnull
	@Override
	public String getName()
	{
		return "clearKnowledge";
	}
	
	@Nonnull
	@Override
	public String getUsage(@Nonnull ICommandSender sender)
	{
		return "pe.command.clearknowledge.usage";
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException
	{
		if (params.length < 1)
		{
			throw new WrongUsageException(getUsage(sender));
		}

		for (EntityPlayerMP player : getPlayers(server, sender, params[0]))
		{
			player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).clearKnowledge();
			PacketHandler.sendTo(new KnowledgeClearPKT(), player);
			sender.sendMessage(new TextComponentTranslation("pe.command.clearknowledge.success", player.getName()));

			if (!player.getName().equals(sender.getName()))
			{
				player.sendMessage(new TextComponentTranslation("pe.command.clearknowledge.notify", sender.getName()).setStyle(new Style().setColor(TextFormatting.RED)));
			}
		}
	}

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1)
        {
            List<String> list = new ArrayList<>(Arrays.asList(server.getOnlinePlayerNames()));
            list.addAll(DEFAULT_SELECTORS);
            return getListOfStringsMatchingLastWord(args, list);
        } else
        {
            return Collections.emptyList();
        }
    }

    @Override
	public int getRequiredPermissionLevel() 
	{
		return 4;
	}
}
