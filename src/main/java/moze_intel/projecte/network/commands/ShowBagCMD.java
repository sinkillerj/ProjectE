package moze_intel.projecte.network.commands;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ShowBagPKT;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.UUID;

public class ShowBagCMD extends CommandBase {
	@Override
	public String getName() {
		return "showBag";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "pe.command.showbag.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayerMP))
		{
			throw new CommandException("pe.command.showbag.notplayer");
		}
		if (args.length != 2)
		{
			throw new WrongUsageException("pe.command.showbag.usage");
		}

		EntityPlayerMP senderPlayer = (EntityPlayerMP) sender;
		EnumDyeColor color = EnumDyeColor.WHITE;
		try {
			color = EnumDyeColor.valueOf(args[0].toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException ex) {
			throw new CommandException("pe.command.showbag.nocolor", args[0]);
		}
		EntityPlayerMP target = getPlayer(server, sender, args[1]);
		IItemHandlerModifiable inv = (IItemHandlerModifiable) target.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null).getBag(color);

		senderPlayer.closeScreen();
		senderPlayer.getNextWindowId();
		senderPlayer.openContainer = new AlchBagContainer(senderPlayer.inventory, EnumHand.OFF_HAND, inv)
		{
			@Override
			public boolean canInteractWith(@Nonnull EntityPlayer player)
			{
				return target.isEntityAlive() && !target.hasDisconnected();
			}
		};
		senderPlayer.openContainer.windowId = senderPlayer.currentWindowId;
		PacketHandler.sendTo(new ShowBagPKT(senderPlayer.openContainer.windowId), senderPlayer);
		senderPlayer.openContainer.addListener(senderPlayer);


	}
}
