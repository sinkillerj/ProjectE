package moze_intel.projecte.network.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.item.EnumDyeColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ColorArgument implements ArgumentType<EnumDyeColor>
{
    private static final List<String> EXAMPLES = Arrays.asList("red", "brown", "light_gray");

    @Override
    public EnumDyeColor parse(StringReader reader) throws CommandSyntaxException
    {
        String s = reader.readUnquotedString();
        for (EnumDyeColor c : EnumDyeColor.values())
        {
            if (c.getName().equals(s))
            {
                return c;
            }
        }

        throw net.minecraft.command.arguments.ColorArgument.COLOR_INVALID.create(s);
    }

    public static EnumDyeColor getColor(CommandContext<CommandSource> context, String name)
    {
        return context.getArgument(name, EnumDyeColor.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder)
    {
        return ISuggestionProvider.suggest(Arrays.stream(EnumDyeColor.values()).map(EnumDyeColor::getName), builder);
    }

    @Override
    public Collection<String> getExamples()
    {
        return EXAMPLES;
    }
}
