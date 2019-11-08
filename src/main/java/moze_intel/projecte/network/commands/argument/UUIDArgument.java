package moze_intel.projecte.network.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.UUID;
import net.minecraft.util.text.TranslationTextComponent;

public class UUIDArgument implements ArgumentType<UUID> {

	private static final DynamicCommandExceptionType MALFORMED_UUID = new DynamicCommandExceptionType(uuid ->
			new TranslationTextComponent("pe.command.showbag.offline.uuid", uuid));

	@Override
	public UUID parse(StringReader reader) throws CommandSyntaxException {
		String s = reader.readUnquotedString();
		try {
			return UUID.fromString(s);
		} catch (IllegalArgumentException e) {
			throw MALFORMED_UUID.create(s);
		}
	}

	public static <S> UUID getUUID(CommandContext<S> context, String name) {
		return context.getArgument(name, UUID.class);
	}
}