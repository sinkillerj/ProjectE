package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import moze_intel.projecte.PEPermissions;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.config.CustomEMCParser;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagKeyArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SetEmcCMD {

	static final DynamicCommandExceptionType ERROR_INVALID_ITEM = new DynamicCommandExceptionType(
			item -> Component.translatableEscape("argument.item.id.invalid", item)
	);

	public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext context) {
		return Commands.literal("setemc")
				.requires(PEPermissions.COMMAND_SET_EMC)
				.then(Commands.argument("emc", LongArgumentType.longArg(0, Long.MAX_VALUE))
						.then(Commands.argument("item", ItemArgument.item(context))
								.executes(ctx -> {
									ItemInput itemInput = ItemArgument.getItem(ctx, "item");
									return setEmc(ctx, NSSItem.createItem(itemInput.createItemStack(1)), LongArgumentType.getLong(ctx, "emc"));
								}))
						.then(Commands.argument("tag", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM))
								.executes(ctx -> {
									Either<ResourceKey<Item>, TagKey<Item>> result = ResourceOrTagKeyArgument.getResourceOrTagKey(ctx, "tag", Registries.ITEM, ERROR_INVALID_ITEM).unwrap();
									return setEmc(ctx, result.map(NSSItem::createItem, NSSItem::createTag), LongArgumentType.getLong(ctx, "emc"));
								}))
						.executes(ctx -> setEmc(ctx, RemoveEmcCMD.getHeldStack(ctx), LongArgumentType.getLong(ctx, "emc"))));

	}

	private static int setEmc(CommandContext<CommandSourceStack> ctx, NSSItem toSet, long emc) {
		CustomEMCParser.addToFile(toSet, emc);
		ctx.getSource().sendSuccess(() -> PELang.COMMAND_SET_SUCCESS.translate(toSet, emc), true);
		ctx.getSource().sendSuccess(PELang.RELOAD_NOTICE::translate, true);
		return Command.SINGLE_SUCCESS;
	}
}
