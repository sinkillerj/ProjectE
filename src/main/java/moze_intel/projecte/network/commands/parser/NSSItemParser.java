package moze_intel.projecte.network.commands.parser;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import moze_intel.projecte.utils.RegistryUtils;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Customized version of {@link net.minecraft.commands.arguments.item.ItemParser} that does not support NBT on tags, and does not wrap it into a Predicate.
 */
public class NSSItemParser {

	//This error message is a copy of ItemParser ERROR_UNKNOWN_ITEM and ERROR_UNKNOWN_TAG
	private static final DynamicCommandExceptionType UNKNOWN_ITEM = new DynamicCommandExceptionType(PELang.UNKNOWN_ITEM::translate);
	private static final DynamicCommandExceptionType UNKNOWN_TAG = new DynamicCommandExceptionType(PELang.UNKNOWN_TAG::translate);
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
	private static final char SYNTAX_START_NBT = '{';
	private static final char SYNTAX_TAG = '#';

	private final HolderLookup<Item> items;
	private final StringReader reader;
	private Either<Holder<Item>, ResourceLocation> result;
	@Nullable
	private CompoundTag nbt;
	/** Builder to be used when creating a list of suggestions */
	private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

	public NSSItemParser(HolderLookup<Item> items, StringReader readerIn) {
		this.items = items;
		this.reader = readerIn;
	}

	public static NSSItemResult parseResult(HolderLookup<Item> items, StringReader reader) throws CommandSyntaxException {
		int cursor = reader.getCursor();
		try {
			NSSItemParser nssItemParser = new NSSItemParser(items, reader);
			nssItemParser.parse();
			return nssItemParser.result.map(item -> new ItemResult(item, nssItemParser.nbt), TagResult::new);
		} catch (CommandSyntaxException e) {
			reader.setCursor(cursor);
			throw e;
		}
	}

	/**
	 * Create a list of suggestions for the specified builder.
	 *
	 * @param builder Builder to create list of suggestions
	 */
	public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Item> items, SuggestionsBuilder builder) {
		StringReader reader = new StringReader(builder.getInput());
		reader.setCursor(builder.getStart());
		NSSItemParser parser = new NSSItemParser(items, reader);
		try {
			parser.parse();
		} catch (CommandSyntaxException ignored) {
		}
		return parser.suggestions.apply(builder.createOffset(reader.getCursor()));
	}

	private void parse() throws CommandSyntaxException {
		this.suggestions = this::suggestTagOrItem;
		int cursor = this.reader.getCursor();
		if (this.reader.canRead() && this.reader.peek() == SYNTAX_TAG) {
			//Read Tag
			this.reader.expect(SYNTAX_TAG);
			this.suggestions = this::suggestTag;
			ResourceLocation name = ResourceLocation.read(this.reader);
			Optional<? extends HolderSet<Item>> tag = this.items.get(TagKey.create(Registries.ITEM, name));
			tag.orElseThrow(() -> {
				//If it isn't present reset and error
				this.reader.setCursor(cursor);
				return UNKNOWN_TAG.createWithContext(this.reader, name);
			});
			this.result = Either.right(name);
		} else {
			//Read Item
			ResourceLocation name = ResourceLocation.read(this.reader);
			Optional<Holder.Reference<Item>> item = this.items.get(ResourceKey.create(Registries.ITEM, name));
			this.result = Either.left(item.orElseThrow(() -> {
				this.reader.setCursor(cursor);
				return UNKNOWN_ITEM.createWithContext(this.reader, name);
			}));
			this.suggestions = this::suggestOpenNbt;
			if (this.reader.canRead() && this.reader.peek() == SYNTAX_START_NBT) {
				this.suggestions = SUGGEST_NOTHING;
				this.nbt = new TagParser(this.reader).readStruct();
			}
		}
	}

	private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder builder) {
		if (builder.getRemaining().isEmpty()) {
			builder.suggest(String.valueOf(SYNTAX_START_NBT));
		}
		return builder.buildFuture();
	}

	/**
	 * Builds a list of suggestions based on item tags.
	 *
	 * @param builder Builder to create list of suggestions
	 */
	private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(this.items.listTags().map(reference -> reference.key().location()), builder, String.valueOf(SYNTAX_TAG));
	}

	private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(this.items.listElements().map(reference -> reference.key().location()), builder);
	}

	/**
	 * Builds a list of suggestions based on item tags (if the parser is set to allows tags) and item registry names.
	 *
	 * @param builder Builder to create list of suggestions
	 */
	private CompletableFuture<Suggestions> suggestTagOrItem(SuggestionsBuilder builder) {
		suggestTag(builder);
		return suggestItem(builder);
	}

	public static NSSItemResult resultOf(ItemStack stack) {
		return new ItemResult(stack.getItem(), stack.getTag());
	}

	public interface NSSItemResult {

		String getStringRepresentation();
	}

	private record ItemResult(Item item, @Nullable CompoundTag nbt) implements NSSItemResult {

		public ItemResult(Holder<Item> item, @Nullable CompoundTag nbt) {
			this(item.value(), nbt);
		}

		@Override
		public String getStringRepresentation() {
			String registryName = BuiltInRegistries.ITEM.getKey(item).toString();
			if (nbt == null) {
				return registryName;
			}
			return registryName + nbt;
		}
	}

	private record TagResult(ResourceLocation tagName) implements NSSItemResult {

		@Override
		public String getStringRepresentation() {
			return SYNTAX_TAG + tagName.toString();
		}
	}
}