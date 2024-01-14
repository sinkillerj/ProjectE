package moze_intel.projecte.network.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.network.commands.parser.NSSItemParser;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class NSSItemArgument implements ArgumentType<NSSItem> {

	private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "minecraft:stick{foo=bar}", "#minecraft:wool");

	private final HolderLookup<Item> items;

	private NSSItemArgument(CommandBuildContext context) {
		this.items = context.holderLookup(Registries.ITEM);
	}

	public static NSSItemArgument nss(CommandBuildContext context) {
		return new NSSItemArgument(context);
	}

	@Override
	public NSSItem parse(StringReader reader) throws CommandSyntaxException {
		return NSSItemParser.parseResult(this.items, reader);
	}

	public static <S> NSSItem getNSS(CommandContext<S> context, String name) {
		return context.getArgument(name, NSSItem.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return NSSItemParser.fillSuggestions(this.items, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}