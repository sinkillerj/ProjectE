package moze_intel.projecte.network.commands.parser;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Customized version of {@link net.minecraft.command.arguments.ItemParser} that does not support NBT on tags, and does not wrap it into a Predicate.
 */
public class NSSItemParser {

	//This error message is a copy of ItemPredicateArgument.UNKNOWN_TAG
	private static final DynamicCommandExceptionType UNKNOWN_TAG = new DynamicCommandExceptionType(tagId -> new TranslationTextComponent("arguments.item.tag.unknown", tagId));
	private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> DEFAULT_SUGGESTIONS_BUILDER = SuggestionsBuilder::buildFuture;

	private final StringReader reader;
	@Nullable
	private Item item;
	@Nullable
	private CompoundNBT nbt;
	private ResourceLocation tagId = new ResourceLocation("");
	private int readerCursor;
	/** Builder to be used when creating a list of suggestions */
	private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestionsBuilder = DEFAULT_SUGGESTIONS_BUILDER;

	public NSSItemParser(StringReader readerIn) {
		this.reader = readerIn;
	}

	public NSSItemResult getResult() throws CommandSyntaxException {
		if (item != null) {
			return new NSSItemResult(this);
		}
		//Else it is a tag
		ITag<Item> tag = ItemTags.getCollection().get(tagId);
		if (tag == null) {
			throw UNKNOWN_TAG.create(tagId.toString());
		}
		return new NSSItemResult(this);
	}

	public NSSItemParser parse() throws CommandSyntaxException {
		this.suggestionsBuilder = this::suggestTagOrItem;
		if (this.reader.canRead() && this.reader.peek() == '#') {
			//Read Tag
			this.suggestionsBuilder = this::suggestTag;
			this.reader.expect('#');
			this.readerCursor = this.reader.getCursor();
			this.tagId = ResourceLocation.read(this.reader);
		} else {
			//Read Item
			int i = this.reader.getCursor();
			ResourceLocation itemId = ResourceLocation.read(this.reader);
			item = ForgeRegistries.ITEMS.getValue(itemId);
			if (item == null) {
				this.reader.setCursor(i);
				throw ItemParser.ITEM_BAD_ID.createWithContext(this.reader, itemId);
			}
			this.suggestionsBuilder = this::suggestItem;
			if (this.reader.canRead() && this.reader.peek() == '{') {
				this.suggestionsBuilder = DEFAULT_SUGGESTIONS_BUILDER;
				this.nbt = new JsonToNBT(this.reader).readStruct();
			}
		}
		return this;
	}

	/**
	 * Builds a list of suggestions based on item registry names.
	 *
	 * @param builder Builder to create list of suggestions
	 */
	private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder builder) {
		if (builder.getRemaining().isEmpty()) {
			builder.suggest(String.valueOf('{'));
		}
		return builder.buildFuture();
	}

	/**
	 * Builds a list of suggestions based on item tags.
	 *
	 * @param builder Builder to create list of suggestions
	 */
	private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder builder) {
		return ISuggestionProvider.suggestIterable(ItemTags.getCollection().getRegisteredTags(), builder.createOffset(this.readerCursor));
	}

	/**
	 * Builds a list of suggestions based on item tags (if the parser is set to allows tags) and item registry names.
	 *
	 * @param builder Builder to create list of suggestions
	 */
	private CompletableFuture<Suggestions> suggestTagOrItem(SuggestionsBuilder builder) {
		ISuggestionProvider.suggestIterable(ItemTags.getCollection().getRegisteredTags(), builder, String.valueOf('#'));
		return ISuggestionProvider.suggestIterable(ForgeRegistries.ITEMS.getKeys(), builder);
	}

	/**
	 * Create a list of suggestions for the specified builder.
	 *
	 * @param builder Builder to create list of suggestions
	 */
	public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder builder) {
		return this.suggestionsBuilder.apply(builder.createOffset(this.reader.getCursor()));
	}

	public static class NSSItemResult {

		@Nullable
		private Item item;
		@Nullable
		private CompoundNBT nbt;
		private ResourceLocation tagId = new ResourceLocation("");

		public NSSItemResult(NSSItemParser parser) {
			item = parser.item;
			nbt = parser.nbt;
			tagId = parser.tagId;
		}

		public NSSItemResult(@Nonnull ItemStack stack) {
			item = stack.getItem();
			nbt = stack.getTag();
		}

		public String getStringRepresentation() {
			if (item == null) {
				return "#" + tagId;
			}
			if (nbt == null) {
				return item.getRegistryName().toString();
			}
			return item.getRegistryName().toString() + nbt;
		}
	}
}