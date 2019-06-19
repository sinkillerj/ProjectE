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
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.impl.AlchBagImpl;
import moze_intel.projecte.network.PacketHandler;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.network.NetworkHooks;
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

	private static int showBag(CommandContext<CommandSource> ctx, DyeColor color, ServerPlayerEntity player) throws CommandSyntaxException
	{
		ServerPlayerEntity senderPlayer = ctx.getSource().asPlayer();
		NetworkHooks.openGui(senderPlayer, createContainer(senderPlayer, player, color), b -> {
			b.writeBoolean(false);
			b.writeBoolean(false);
		});
		return Command.SINGLE_SUCCESS;
	}

	private static INamedContainerProvider createContainer(ServerPlayerEntity sender, ServerPlayerEntity target, DyeColor color) throws CommandException
	{
		IItemHandlerModifiable inv = (IItemHandlerModifiable) target.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY)
				.orElseThrow(NullPointerException::new)
				.getBag(color);
		ITextComponent name = new TranslationTextComponent(ObjHandler.getBag(color).getTranslationKey())
				.appendText(" (")
				.appendSibling(target.getDisplayName())
				.appendText(")");

		return new INamedContainerProvider() {
			@Nonnull
			@Override
			public ITextComponent getDisplayName()
			{
				return name;
			}

			@Override
			public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player)
			{
				return new AlchBagContainer(windowId, sender.inventory, Hand.OFF_HAND, inv, false)
				{
					@Override
					public boolean canInteractWith(@Nonnull PlayerEntity player)
					{
						return target.isAlive() && !target.hasDisconnected();
					}
				};
			}
		};



	/*	UUID uuid; todo 1.13
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
