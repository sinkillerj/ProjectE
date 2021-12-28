package moze_intel.projecte.network.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.world.item.DyeColor;

public class ColorArgument implements ArgumentType<DyeColor> {

	private static final List<String> EXAMPLES = Arrays.asList("red", "brown", "light_gray");

	@Override
	public DyeColor parse(StringReader reader) throws CommandSyntaxException {
		String s = reader.readUnquotedString();
		for (DyeColor c : DyeColor.values()) {
			if (c.getSerializedName().equals(s)) {
				return c;
			}
		}
		throw net.minecraft.commands.arguments.ColorArgument.ERROR_INVALID_VALUE.create(s);
	}

	public static <S> DyeColor getColor(CommandContext<S> context, String name) {
		return context.getArgument(name, DyeColor.class);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(Arrays.stream(DyeColor.values()).map(DyeColor::getSerializedName), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}