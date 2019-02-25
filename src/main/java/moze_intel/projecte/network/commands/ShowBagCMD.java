package moze_intel.projecte.network.commands;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.impl.AlchBagImpl;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ShowBagPKT;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
		EnumDyeColor color;
		try {
			color = EnumDyeColor.valueOf(args[0].toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException ex) {
			throw new CommandException("pe.command.showbag.nocolor", args[0]);
		}

		senderPlayer.closeScreen();
		senderPlayer.getNextWindowId();
		senderPlayer.openContainer = createContainer(server, senderPlayer, args[1], color);
		senderPlayer.openContainer.windowId = senderPlayer.currentWindowId;
		PacketHandler.sendTo(new ShowBagPKT(senderPlayer.openContainer.windowId), senderPlayer);
		senderPlayer.openContainer.addListener(senderPlayer);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, Arrays.asList(EnumDyeColor.values()));
		} else if (args.length == 2)
		{
			return getListOfStringsMatchingLastWord(args, Arrays.asList(server.getOnlinePlayerNames()));
		} else
		{
			return Collections.emptyList();
		}
	}

	private static Container createContainer(MinecraftServer server, EntityPlayerMP sender, String playerArg, EnumDyeColor color) throws CommandException
	{
		try
		{
			EntityPlayerMP target = getPlayer(server, sender, playerArg);
			IItemHandlerModifiable inv = (IItemHandlerModifiable) target.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY, null).getBag(color);
			return new AlchBagContainer(sender.inventory, EnumHand.OFF_HAND, inv)
			{
				@Override
				public boolean canInteractWith(@Nonnull EntityPlayer player)
				{
					return target.isEntityAlive() && !target.hasDisconnected();
				}
			};
		} catch (PlayerNotFoundException ignored) {}

		UUID uuid;
		try
		{
			uuid = UUID.fromString(playerArg);
		} catch (IllegalArgumentException ex)
		{
			throw new CommandException("pe.command.showbag.offline.uuid");
		}
		IItemHandlerModifiable inv = loadOfflineBag(uuid, color);
		return new AlchBagContainer(sender.inventory, EnumHand.OFF_HAND, inv, true);
	}

	private static IItemHandlerModifiable loadOfflineBag(UUID playerUUID, EnumDyeColor color) throws CommandException
	{
		File playerData = new File(DimensionManager.getCurrentSaveRootDirectory(), "playerdata");
		if (playerData.exists())
		{
			File player = new File(playerData, playerUUID.toString() + ".dat");
			if (player.exists() && player.isFile()) {
				try(FileInputStream in = new FileInputStream(player)) {
					NBTTagCompound playerDat = CompressedStreamTools.readCompressed(in);
					NBTTagCompound bagProvider = playerDat.getCompoundTag("ForgeCaps").getCompoundTag(AlchBagImpl.Provider.NAME.toString());

					IAlchBagProvider provider = ProjectEAPI.ALCH_BAG_CAPABILITY.getDefaultInstance();
					ProjectEAPI.ALCH_BAG_CAPABILITY.readNBT(provider, null, bagProvider);

					return (IItemHandlerModifiable) provider.getBag(color);
				} catch (IOException e) {
					// fall through to below
				}
			}
		}
		throw new CommandException("pe.command.showbag.offline.notfound", playerUUID.toString());
	}
}
