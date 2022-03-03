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
import moze_intel.projecte.network.commands.parser.NSSItemParser;
import moze_intel.projecte.network.commands.parser.NSSItemParser.NSSItemResult;
import net.minecraft.core.Registry;

public class NSSItemArgument implements ArgumentType<NSSItemResult> {

	private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");

	@Override
	public NSSItemResult parse(StringReader reader) throws CommandSyntaxException {
		return new NSSItemParser(reader).parse().getResult();
	}

	public static <S> NSSItemResult getNSS(CommandContext<S> context, String name) {
		return context.getArgument(name, NSSItemResult.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		StringReader reader = new StringReader(builder.getInput());
		reader.setCursor(builder.getStart());
		NSSItemParser parser = new NSSItemParser(reader);
		try {
			parser.parse();
		} catch (CommandSyntaxException ignored) {
		}
		return parser.fillSuggestions(builder, Registry.ITEM);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}