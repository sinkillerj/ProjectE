package moze_intel.projecte.network.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ResetEmcCMD {

	private static final DynamicCommandExceptionType INVALID_ITEM = new DynamicCommandExceptionType(PELang.COMMAND_INVALID_ITEM::translate);

	public static LiteralArgumentBuilder<CommandSourceStack> register(CommandBuildContext context) {
		return Commands.literal("resetemc")
				.requires(PEPermissions.COMMAND_RESET_EMC)
				.then(Commands.argument("item", ItemArgument.item(context))
						.executes(ctx -> {
							ItemInput itemInput = ItemArgument.getItem(ctx, "item");
							return resetEmc(ctx, NSSItem.createItem(itemInput.createItemStack(1)));
						}))
				.then(Commands.argument("tag", ResourceOrTagKeyArgument.resourceOrTagKey(Registries.ITEM))
						.executes(ctx -> {
							Either<ResourceKey<Item>, TagKey<Item>> result = ResourceOrTagKeyArgument.getResourceOrTagKey(ctx, "tag", Registries.ITEM, SetEmcCMD.ERROR_INVALID_ITEM).unwrap();
							return resetEmc(ctx, result.map(NSSItem::createItem, NSSItem::createTag));
						}))
				.executes(ctx -> resetEmc(ctx, RemoveEmcCMD.getHeldStack(ctx)));
	}

	private static int resetEmc(CommandContext<CommandSourceStack> ctx, NSSItem toReset) throws CommandSyntaxException {
		if (CustomEMCParser.removeFromFile(toReset)) {
			ctx.getSource().sendSuccess(() -> PELang.COMMAND_RESET_SUCCESS.translate(toReset), true);
			ctx.getSource().sendSuccess(PELang.RELOAD_NOTICE::translate, true);
			return Command.SINGLE_SUCCESS;
		}
		throw INVALID_ITEM.create(toReset);
	}
}
