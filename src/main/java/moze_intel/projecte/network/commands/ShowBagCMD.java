package moze_intel.projecte.network.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IAlchBagProvider;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.impl.capability.AlchBagImpl;
import moze_intel.projecte.network.commands.argument.ColorArgument;
import moze_intel.projecte.network.commands.argument.UUIDArgument;
import moze_intel.projecte.utils.text.PELang;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.server.ServerLifecycleHooks;

public class ShowBagCMD {

	private static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(PELang.SHOWBAG_NOT_FOUND.translate());

	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal("showbag")
				.requires(cs -> cs.hasPermission(2))
				.then(Commands.argument("color", new ColorArgument())
						.then(Commands.argument("target", EntityArgument.player())
								.executes(ctx -> showBag(ctx, ColorArgument.getColor(ctx, "color"), EntityArgument.getPlayer(ctx, "target"))))
						.then(Commands.argument("uuid", new UUIDArgument())
								.executes(ctx -> showBag(ctx, ColorArgument.getColor(ctx, "color"), UUIDArgument.getUUID(ctx, "uuid")))));
	}

	private static int showBag(CommandContext<CommandSourceStack> ctx, DyeColor color, ServerPlayer player) throws CommandSyntaxException {
		ServerPlayer senderPlayer = ctx.getSource().getPlayerOrException();
		return showBag(senderPlayer, createContainer(senderPlayer, player, color));
	}

	private static int showBag(CommandContext<CommandSourceStack> ctx, DyeColor color, UUID uuid) throws CommandSyntaxException {
		ServerPlayer senderPlayer = ctx.getSource().getPlayerOrException();
		return showBag(senderPlayer, createContainer(senderPlayer, uuid, color));
	}

	private static int showBag(ServerPlayer senderPlayer, MenuProvider container) {
		NetworkHooks.openGui(senderPlayer, container, b -> {
			b.writeBoolean(false);
			b.writeBoolean(false);
		});
		return Command.SINGLE_SUCCESS;
	}

	private static MenuProvider createContainer(ServerPlayer sender, ServerPlayer target, DyeColor color) {
		IItemHandlerModifiable inv = (IItemHandlerModifiable) target.getCapability(ProjectEAPI.ALCH_BAG_CAPABILITY)
				.orElseThrow(NullPointerException::new)
				.getBag(color);
		Component name = PELang.SHOWBAG_NAMED.translate(PEItems.getBag(color), target.getDisplayName());
		return getContainer(sender, name, inv, false, () -> target.isAlive() && !target.hasDisconnected());
	}

	private static MenuProvider createContainer(ServerPlayer sender, UUID target, DyeColor color) throws CommandSyntaxException {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		//Try to get the bag
		IItemHandlerModifiable inv = loadOfflineBag(server, target, color);
		Component name;
		Optional<GameProfile> profileByUUID = server.getProfileCache().get(target);
		if (profileByUUID.isPresent()) {
			//If we have a cache of the player, include their last known name in the name of the bag
			name = PELang.SHOWBAG_NAMED.translate(PEItems.getBag(color), profileByUUID.get().getName());
		} else {
			name = TextComponentUtil.build(PEItems.getBag(color));
		}
		return getContainer(sender, name, inv, true, () -> true);
	}

	private static MenuProvider getContainer(ServerPlayer sender, Component name, IItemHandlerModifiable inv, boolean immutable,
			BooleanSupplier canInteractWith) {
		return new MenuProvider() {
			@Nonnull
			@Override
			public Component getDisplayName() {
				return name;
			}

			@Override
			public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInv, @Nonnull Player player) {
				//Note: Selected is unused for offhand
				return new AlchBagContainer(windowId, sender.getInventory(), InteractionHand.OFF_HAND, inv, 0, immutable) {
					@Override
					public boolean stillValid(@Nonnull Player player) {
						return canInteractWith.getAsBoolean();
					}
				};
			}
		};
	}

	private static IItemHandlerModifiable loadOfflineBag(MinecraftServer server, UUID playerUUID, DyeColor color) throws CommandSyntaxException {
		File playerData = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
		if (playerData.exists()) {
			File player = new File(playerData, playerUUID.toString() + ".dat");
			if (player.exists() && player.isFile()) {
				try (FileInputStream in = new FileInputStream(player)) {
					CompoundTag playerDat = NbtIo.readCompressed(in);
					CompoundTag bagProvider = playerDat.getCompound("ForgeCaps").getCompound(AlchBagImpl.Provider.NAME.toString());

					IAlchBagProvider provider = AlchBagImpl.getDefault();
					provider.deserializeNBT(bagProvider);

					return (IItemHandlerModifiable) provider.getBag(color);
				} catch (IOException e) {
					// fall through to below
				}
			}
		}
		throw NOT_FOUND.create();
	}
}