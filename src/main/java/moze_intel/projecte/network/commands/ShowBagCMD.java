package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.impl.AlchBagImpl;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.network.packets.ShowBagPKT;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

public class ShowBagCMD
{
	public static LiteralArgumentBuilder<CommandSource> register()
	{
		return Commands.literal("showbag")
				.then(Commands.argument("color", new ColorArgument())
					// todo 1.13 accept uuid for offline usage
					.then(Commands.argument("target", EntityArgument.player())
							.executes(ctx -> showBag(ctx, ColorArgument.getColor(ctx, "color"), EntityArgument.getPlayer(ctx, "target")))));
	}

	private static int showBag(CommandContext<CommandSource> ctx, EnumDyeColor color, EntityPlayerMP player) throws CommandSyntaxException
	{
		EntityPlayerMP senderPlayer = ctx.getSource().asPlayer();
		senderPlayer.closeScreen();
		senderPlayer.getNextWindowId();
		senderPlayer.openContainer = createContainer(senderPlayer, player, color);
		senderPlayer.openContainer.windowId = senderPlayer.currentWindowId;
		PacketHandler.sendTo(new ShowBagPKT(senderPlayer.openContainer.windowId), senderPlayer);
		senderPlayer.openContainer.addListener(senderPlayer);
		return Command.SINGLE_SUCCESS;
	}

	private static Container createContainer(EntityPlayerMP sender, EntityPlayerMP target, EnumDyeColor color) throws CommandException
	{
			IItemHandlerModifiable inv = (IItemHandlerModifiable) target.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY)
					.orElseThrow(NullPointerException::new)
					.getBag(color);
			return new AlchBagContainer(sender.inventory, EnumHand.OFF_HAND, inv)
			{
				@Override
				public boolean canInteractWith(@Nonnull EntityPlayer player)
				{
					return target.isAlive() && !target.hasDisconnected();
				}
			};

	/*	UUID uuid;
		try
		{
			uuid = UUID.fromString(playerArg);
		} catch (IllegalArgumentException ex)
		{
			throw new CommandException("pe.command.showbag.offline.uuid");
		}
		IItemHandlerModifiable inv = loadOfflineBag(uuid, color);
		return new AlchBagContainer(sender.inventory, EnumHand.OFF_HAND, inv, true);*/
	}

	/*private static IItemHandlerModifiable loadOfflineBag(UUID playerUUID, EnumDyeColor color) throws CommandException
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
	}*/
}
