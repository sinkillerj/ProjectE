package moze_intel.projecte.network.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import moze_intel.projecte.PEPermissions;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.container.AlchBagContainer;
import moze_intel.projecte.gameObjs.registries.PEAttachmentTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.impl.capability.AlchBagImpl.AlchemicalBagAttachment;
import moze_intel.projecte.network.commands.argument.ColorArgument;
import moze_intel.projecte.utils.text.PELang;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
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
import net.neoforged.neoforge.attachment.AttachmentHolder;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class ShowBagCMD {

	private static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(PELang.SHOWBAG_NOT_FOUND.translate());

	public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext context) {
		return Commands.literal("showbag")
				.requires(PEPermissions.COMMAND_SHOW_BAG)
				.then(Commands.argument("color", ColorArgument.color())
						.then(Commands.argument("target", EntityArgument.player())
								.executes(ctx -> showBag(ctx, ColorArgument.getColor(ctx, "color"), EntityArgument.getPlayer(ctx, "target"))))
						.then(Commands.argument("uuid", UuidArgument.uuid())
								.executes(ctx -> showBag(ctx, ColorArgument.getColor(ctx, "color"), UuidArgument.getUuid(ctx, "uuid")))));
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
		senderPlayer.openMenu(container, b -> {
			b.writeBoolean(false);
			b.writeBoolean(false);
		});
		return Command.SINGLE_SUCCESS;
	}

	private static MenuProvider createContainer(ServerPlayer sender, ServerPlayer target, DyeColor color) {
		IItemHandlerModifiable inv = (IItemHandlerModifiable) Objects.requireNonNull(target.getCapability(PECapabilities.ALCH_BAG_CAPABILITY)).getBag(color);
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
			@NotNull
			@Override
			public Component getDisplayName() {
				return name;
			}

			@Override
			public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInv, @NotNull Player player) {
				//Note: Selected is unused for offhand
				return new AlchBagContainer(windowId, sender.getInventory(), InteractionHand.OFF_HAND, inv, 0, immutable) {
					@Override
					public boolean stillValid(@NotNull Player player) {
						return canInteractWith.getAsBoolean();
					}
				};
			}
		};
	}

	private static IItemHandlerModifiable loadOfflineBag(MinecraftServer server, UUID playerUUID, DyeColor color) throws CommandSyntaxException {
		Path player = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).resolve(playerUUID.toString() + ".dat");
		if (Files.exists(player) && Files.isRegularFile(player)) {
			try (InputStream in = Files.newInputStream(player)) {
				CompoundTag playerDat = NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
				if (playerDat.contains(AttachmentHolder.ATTACHMENTS_NBT_KEY, Tag.TAG_COMPOUND)) {
					CompoundTag attachmentData = playerDat.getCompound(AttachmentHolder.ATTACHMENTS_NBT_KEY);
					//TODO - 1.20.4: TEST THIS, and also see how it behaves in regards to mutating? As I think this is used at times regardless?
					AlchemicalBagAttachment attachment = new AlchemicalBagAttachment();
					attachment.deserializeNBT(attachmentData.getCompound(PEAttachmentTypes.ALCHEMICAL_BAGS.getId().toString()));

					return attachment.getBag(color);
				}
			} catch (IOException e) {
				// fall through to below
			}
		}
		throw NOT_FOUND.create();
	}
}